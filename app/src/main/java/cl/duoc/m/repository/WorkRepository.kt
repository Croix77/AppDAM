package cl.duoc.m.repository

import android.content.ContentValues
import android.database.Cursor
import cl.duoc.m.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Trabajos con entrega escrita y corrección humana. No cambian las pruebas automáticas.
class WorkRepository(private val helper: SchoolDatabase) {
    private val db get()=helper.writableDatabase
    private val school=SchoolRepository(helper)
    private fun now()=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    private fun Cursor.s(name: String)=getString(getColumnIndexOrThrow(name)).orEmpty()
    private fun Cursor.n(name: String)=getLong(getColumnIndexOrThrow(name))
    private fun requireRole(actor: Long,role: Role) {require(school.getUser(actor)?.role==role) {"No tienes permiso para esta acción"}}
    private fun task(c: Cursor)=SchoolWork(c.n("id"),c.n("teacher_id"),c.s("course"),c.s("subject"),c.s("title"),c.s("instructions"),c.s("due_date"),c.n("assigned").toInt(),c.n("delivered").toInt(),c.n("reviewed").toInt())
    private val query="""SELECT w.*,
        (SELECT COUNT(*) FROM work_students a WHERE a.work_id=w.id) AS assigned,
        (SELECT COUNT(*) FROM work_submissions s WHERE s.work_id=w.id AND s.submitted_at<>'') AS delivered,
        (SELECT COUNT(*) FROM work_submissions s WHERE s.work_id=w.id AND s.review_published=1) AS reviewed
        FROM school_work w"""
    fun works(actor: Long): List<SchoolWork> {
        val role=school.getUser(actor)?.role
        require(role in listOf(Role.DOCENTE,Role.ESTUDIANTE)) {"Acceso reservado a docentes y estudiantes"}
        val where=if(role==Role.DOCENTE) "w.teacher_id=?" else "EXISTS (SELECT 1 FROM work_students a WHERE a.work_id=w.id AND a.student_id=?)"
        return db.rawQuery("$query WHERE $where ORDER BY w.due_date,w.id DESC",arrayOf(actor.toString())).use {c -> buildList {while(c.moveToNext()) add(task(c))}}
    }
    fun work(actor: Long,id: Long)=works(actor).firstOrNull {it.id==id} ?: throw IllegalArgumentException("Este trabajo no está asignado a tu cuenta")
    private fun requireTeacher(actor: Long,id: Long,studentId: Long?=null): SchoolWork {
        requireRole(actor,Role.DOCENTE)
        val task=work(actor,id)
        if(studentId!=null) {
            require(school.getUser(studentId)?.teacherId==actor) {"Este estudiante no está asignado a tu curso"}
            require(db.rawQuery("SELECT 1 FROM work_students WHERE work_id=? AND student_id=?",arrayOf(id.toString(),studentId.toString())).use {it.moveToFirst()}) {"El estudiante no tiene este trabajo asignado"}
        }
        return task
    }
    private fun readSubmission(workId: Long,studentId: Long,hideDraftReview: Boolean): WorkSubmission? = db.rawQuery("SELECT * FROM work_submissions WHERE work_id=? AND student_id=?",arrayOf(workId.toString(),studentId.toString())).use {c ->
        if(!c.moveToFirst()) null else {
            val published=c.n("review_published")==1L
            val hidden=hideDraftReview && !published
            WorkSubmission(workId,studentId,c.s("response"),c.s("submitted_at"),if(hidden || c.isNull(c.getColumnIndexOrThrow("grade_tenths"))) null else c.n("grade_tenths").toInt(),if(hidden) "" else c.s("feedback"),published,if(hidden) "" else c.s("reviewed_at"))
        }
    }
    fun submission(actor: Long,workId: Long,studentId: Long): WorkSubmission? {
        if(school.getUser(actor)?.role==Role.ESTUDIANTE) {require(actor==studentId) {"Solo puedes ver tu entrega"};work(actor,workId)}
        else requireTeacher(actor,workId,studentId)
        val result=readSubmission(workId,studentId,actor==studentId)
        // El borrador de respuesta pertenece al estudiante; el docente solo recibe entregas.
        return if(actor!=studentId && result?.delivered!=true) null else result
    }
    fun students(actor: Long,id: Long): List<WorkStudent> {
        requireTeacher(actor,id)
        return db.rawQuery("SELECT student_id FROM work_students WHERE work_id=? ORDER BY student_id",arrayOf(id.toString())).use {c -> buildList {
            while(c.moveToNext()) {val student=school.getUser(c.getLong(0)) ?: continue
                if(student.teacherId==actor) add(WorkStudent(student,submission(actor,id,student.id)))
            }
        }}.sortedBy {it.student.name}
    }
    fun create(actor: Long,course: String,subject: String,title: String,instructions: String,due: String): Long {
        requireRole(actor,Role.DOCENTE)
        require(WorkRules.creation(title,instructions,due).isEmpty()) {"Revisa los campos marcados"}
        require(subject in WorkRules.subjects)
        val students=school.students(actor).filter {it.course==course && it.role==Role.ESTUDIANTE}
        require(students.isNotEmpty()) {"Selecciona un curso con estudiantes asignados"}
        db.beginTransaction()
        try {
            val id=db.insertOrThrow("school_work",null,ContentValues().apply {put("teacher_id",actor);put("course",course);put("subject",subject);put("title",title.trim());put("instructions",instructions.trim());put("due_date",due);put("created_at",now())})
            students.forEach {db.execSQL("INSERT INTO work_students VALUES(?,?)",arrayOf(id,it.id))}
            db.setTransactionSuccessful();return id
        } finally {db.endTransaction()}
    }
    fun saveResponse(actor: Long,id: Long,response: String,deliver: Boolean) {
        requireRole(actor,Role.ESTUDIANTE)
        db.beginTransaction()
        try {
            val task=work(actor,id)
            require(LocalDate.now().toString()<=task.dueDate) {"El plazo de entrega terminó"}
            require(WorkRules.response(response)==null) {WorkRules.response(response).orEmpty()}
            require(readSubmission(id,actor,true)?.delivered!=true) {"Este trabajo ya fue entregado"}
            val values=ContentValues().apply {put("work_id",id);put("student_id",actor);put("response",response.trim());put("submitted_at",if(deliver) now() else "")}
            db.insertWithOnConflict("work_submissions",null,values,android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE).also {check(it!=-1L)}
            db.setTransactionSuccessful()
        } finally {db.endTransaction()}
    }
    fun saveReview(actor: Long,id: Long,studentId: Long,grade: String,feedback: String,publish: Boolean) {
        db.beginTransaction()
        try {
            requireTeacher(actor,id,studentId)
            val submission=readSubmission(id,studentId,false)
            require(submission?.delivered==true) {"El estudiante todavía no entrega este trabajo"}
            require(!submission.published) {"La evaluación ya está publicada"}
            require(WorkRules.review(grade,feedback,publish).isEmpty()) {"Revisa la nota y el comentario"}
            val values=ContentValues().apply {
                val parsed=WorkRules.gradeTenths(grade);if(parsed==null) putNull("grade_tenths") else put("grade_tenths",parsed)
                put("feedback",feedback.trim());put("review_published",if(publish) 1 else 0);put("reviewed_at",if(publish) now() else "")
            }
            check(db.update("work_submissions",values,"work_id=? AND student_id=?",arrayOf(id.toString(),studentId.toString()))==1)
            db.setTransactionSuccessful()
        } finally {db.endTransaction()}
    }
}
