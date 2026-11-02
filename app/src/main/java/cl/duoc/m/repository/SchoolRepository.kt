package cl.duoc.m.repository

import android.content.ContentValues
import android.database.Cursor
import cl.duoc.m.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Todas las consultas usan argumentos; ningún texto del usuario se concatena al SQL.
// Las operaciones que deben ser indivisibles se realizan dentro de una transacción.
class SchoolRepository(private val helper: SchoolDatabase) {
    private val db get() = helper.writableDatabase
    private fun now() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    private fun Cursor.str(name: String) = getString(getColumnIndexOrThrow(name)) ?: ""
    private fun Cursor.num(name: String) = getLong(getColumnIndexOrThrow(name))
    private fun <T> rows(sql: String, args: Array<String> = emptyArray(), read: (Cursor) -> T): List<T> = db.rawQuery(sql,args).use { c -> buildList { while(c.moveToNext()) add(read(c)) } }
    private fun user(c: Cursor) = User(c.num("id"),c.str("username"),c.str("name"),Role.valueOf(c.str("role")),c.str("course"),c.str("teacher_id").toLongOrNull(),c.str("photo"))
    private fun lesson(c: Cursor) = Lesson(c.num("id"),c.str("subject"),c.str("title"),c.str("objective"),c.str("explanation"),c.str("example"),UnitStatus.valueOf(c.str("status")),c.str("origin"))
    private fun question(c: Cursor) = Question(c.num("id"),c.num("lesson_id"),c.str("prompt"),listOf(c.str("a"),c.str("b"),c.str("c")),c.num("correct").toInt(),c.str("explanation"),c.num("final_test")==1L,ActivityKind.valueOf(c.str("kind")))
    private fun attempt(c: Cursor) = Attempt(c.num("id"),c.num("student_id"),c.num("lesson_id"),c.num("number").toInt(),c.num("score").toInt(),c.num("total").toInt(),c.str("date"))
    private fun role(id: Long) = getUser(id)?.role ?: error("Usuario no encontrado")
    private fun requireRole(id: Long, expected: Role) { require(role(id) == expected) { "No tienes permiso para esta acción" } }
    private fun requireTeacher(teacherId: Long, studentId: Long) {
        requireRole(teacherId,Role.DOCENTE)
        require(getUser(studentId)?.teacherId == teacherId) { "Este estudiante no está asignado a tu curso" }
    }
    private fun audit(actor: Long, action: String) { db.execSQL("INSERT INTO audit(date,actor,action) VALUES(?,?,?)", arrayOf(now(),getUser(actor)?.username ?: "Sistema",action)) }
    fun login(username: String, password: String): User? = db.rawQuery("SELECT * FROM users WHERE username=?",arrayOf(username.trim().lowercase())).use { c ->
        if(c.moveToFirst() && Passwords.matches(password,c.str("salt"),c.str("password"))) user(c) else null
    }
    fun getUser(id: Long) = rows("SELECT * FROM users WHERE id=?", arrayOf(id.toString()),::user).firstOrNull()
    fun students(teacherId: Long): List<User> { requireRole(teacherId,Role.DOCENTE); return rows("SELECT * FROM users WHERE teacher_id=? ORDER BY name",arrayOf(teacherId.toString()),::user) }
    fun lessons(actor: Long): List<Lesson> = if(role(actor)==Role.ESTUDIANTE) rows("SELECT l.* FROM lessons l JOIN assignments a ON l.id=a.lesson_id WHERE a.student_id=? AND l.status='PUBLICADO' ORDER BY l.id",arrayOf(actor.toString()),::lesson) else rows("SELECT * FROM lessons ORDER BY id",read=::lesson)
    fun getLesson(id: Long) = rows("SELECT * FROM lessons WHERE id=?",arrayOf(id.toString()),::lesson).firstOrNull() ?: error("Unidad no encontrada")
    fun questions(lessonId: Long, finalTest: Boolean) = rows("SELECT * FROM questions WHERE lesson_id=? AND final_test=? ORDER BY id",arrayOf(lessonId.toString(),if(finalTest) "1" else "0"),::question)
    fun assignment(studentId: Long, lessonId: Long) = rows("SELECT * FROM assignments WHERE student_id=? AND lesson_id=?",arrayOf(studentId.toString(),lessonId.toString())) { Assignment(it.num("student_id"),it.num("lesson_id"),it.num("max_attempts").toInt(),it.str("start_date"),it.str("end_date")) }.firstOrNull()
    fun attempts(studentId: Long, lessonId: Long) = rows("SELECT * FROM attempts WHERE student_id=? AND lesson_id=? ORDER BY number",arrayOf(studentId.toString(),lessonId.toString()),::attempt)
    fun practice(studentId: Long, lessonId: Long): List<Answer> = rows("SELECT p.* FROM practice p JOIN questions q ON p.question_id=q.id WHERE p.student_id=? AND q.lesson_id=?",arrayOf(studentId.toString(),lessonId.toString())) { Answer(it.num("question_id"),it.num("choice").toInt(),it.num("correct")==1L) }
    fun available(studentId: Long, lessonId: Long): String? {
        if(role(studentId)!=Role.ESTUDIANTE) return "Solo un estudiante puede responder"
        if(getLesson(lessonId).status!=UnitStatus.PUBLICADO) return "La unidad no está publicada"
        val a=assignment(studentId,lessonId) ?: return "Tu docente aún no te asignó esta unidad"
        val today=LocalDate.now().toString()
        return when { today < a.start -> "Disponible desde ${a.start}"; today > a.end -> "La actividad terminó el ${a.end}"; else -> null }
    }
    fun savePractice(actor: Long, lessonId: Long, questionId: Long, choice: Int) {
        check(available(actor,lessonId)==null) { available(actor,lessonId) ?: "No disponible" }
        val q=questions(lessonId,false).firstOrNull { it.id==questionId } ?: error("Pregunta no encontrada")
        require(choice in q.options.indices)
        db.execSQL("INSERT OR REPLACE INTO practice VALUES(?,?,?,?,?)",arrayOf(actor,questionId,choice,if(choice==q.correct) 1 else 0,now()))
    }
    fun submitTest(actor: Long, lessonId: Long, choices: Map<Long,Int>): Attempt {
        db.beginTransaction()
        try {
            check(available(actor,lessonId)==null) { available(actor,lessonId) ?: "No disponible" }
            val qs=questions(lessonId,true)
            require(qs.isNotEmpty() && choices.keys==qs.map { it.id }.toSet() && qs.all { choices[it.id] in it.options.indices }) { "Responde todas las preguntas" }
            require(practice(actor,lessonId).count { it.correct }==questions(lessonId,false).size) { "Completa correctamente la práctica antes de la prueba" }
            val number=attempts(actor,lessonId).size+1
            require(number <= (assignment(actor,lessonId)?.maxAttempts ?: 0)) { "Ya utilizaste todos los intentos" }
            val score=qs.count { choices[it.id]==it.correct }
            val date=now()
            val values=ContentValues().apply { put("student_id",actor);put("lesson_id",lessonId);put("number",number);put("score",score);put("total",qs.size);put("date",date) }
            val id=db.insertOrThrow("attempts",null,values)
            qs.forEach { q -> db.execSQL("INSERT INTO test_answers VALUES(?,?,?,?)",arrayOf(id,q.id,choices[q.id],if(choices[q.id]==q.correct) 1 else 0)) }
            db.setTransactionSuccessful()
            return Attempt(id,actor,lessonId,number,score,qs.size,date)
        } finally { db.endTransaction() }
    }
    fun testReview(studentId: Long, lessonId: Long): List<Pair<Question,Answer>> {
        val id=attempts(studentId,lessonId).lastOrNull()?.id ?: return emptyList()
        return rows("SELECT q.*,t.choice AS selected,t.correct AS is_correct FROM questions q JOIN test_answers t ON q.id=t.question_id WHERE t.attempt_id=? ORDER BY q.id",arrayOf(id.toString())) { question(it) to Answer(it.num("id"),it.num("selected").toInt(),it.num("is_correct")==1L) }
    }
    fun progress(studentId: Long, lessonId: Long): Int {
        val size=questions(lessonId,false).size+1
        return ((practice(studentId,lessonId).count { it.correct }+if(attempts(studentId,lessonId).isNotEmpty()) 1 else 0)*100)/size
    }
    fun assign(actor: Long, studentId: Long, lessonId: Long, count: Int, start: String, end: String) {
        requireTeacher(actor,studentId)
        require(FormRules.assignment(count.toString(),start,end)==null)
        require(getLesson(lessonId).status in listOf(UnitStatus.AUTORIZADO,UnitStatus.PUBLICADO)) { "UTP debe autorizar la unidad antes de asignarla" }
        require(count>=attempts(studentId,lessonId).size) { "No puedes reducir el límite por debajo de los intentos usados" }
        db.execSQL("INSERT OR REPLACE INTO assignments VALUES(?,?,?,?,?)",arrayOf(studentId,lessonId,count,start,end))
        audit(actor,"Asignó unidad $lessonId a estudiante $studentId: $count intentos, $start a $end")
    }
    fun saveLesson(actor: Long, id: Long?, subject: String, title: String, objective: String, text: String, example: String, origin: String): Long {
        requireRole(actor,Role.UTP)
        require(listOf(subject,title,objective,text,example,origin).all { it.isNotBlank() }) { "Completa todos los campos de la unidad" }
        val values=ContentValues().apply { put("subject",subject);put("title",title.trim());put("objective",objective.trim());put("explanation",text.trim());put("example",example.trim());put("origin",origin.trim());put("status","BORRADOR") }
        val result=if(id==null) db.insertOrThrow("lessons",null,values) else {
            require(getLesson(id).status==UnitStatus.BORRADOR) { "Pasa la unidad a borrador antes de editar" }
            db.update("lessons",values,"id=?",arrayOf(id.toString())); id
        }
        audit(actor,"Guardó borrador de unidad $result")
        return result
    }
    fun setStatus(actor: Long, lessonId: Long, target: UnitStatus) {
        requireRole(actor,Role.UTP)
        val current=getLesson(lessonId).status
        val allowed=when(current) {
            UnitStatus.BORRADOR -> listOf(UnitStatus.EN_REVISION)
            UnitStatus.EN_REVISION -> listOf(UnitStatus.AUTORIZADO,UnitStatus.BORRADOR)
            UnitStatus.AUTORIZADO -> listOf(UnitStatus.PUBLICADO,UnitStatus.BORRADOR)
            UnitStatus.PUBLICADO -> listOf(UnitStatus.DESHABILITADO)
            UnitStatus.DESHABILITADO -> listOf(UnitStatus.PUBLICADO,UnitStatus.BORRADOR)
        }
        require(target in allowed) { "Transición de estado inválida" }
        if(target in listOf(UnitStatus.AUTORIZADO,UnitStatus.PUBLICADO)) require(questions(lessonId,false).isNotEmpty() && questions(lessonId,true).isNotEmpty()) { "Agrega al menos una pregunta de práctica y una de prueba" }
        db.execSQL("UPDATE lessons SET status=? WHERE id=?",arrayOf(target.name,lessonId))
        audit(actor,"Unidad $lessonId: $current → $target")
    }
    fun saveQuestion(actor: Long, lessonId: Long, prompt: String, options: List<String>, correct: Int, explanation: String, finalTest: Boolean) {
        requireRole(actor,Role.UTP)
        require(getLesson(lessonId).status==UnitStatus.BORRADOR) { "Solo se agregan preguntas a borradores" }
        require(FormRules.question(prompt,options,explanation)==null && correct in 0..2) { "Revisa la pregunta y sus alternativas" }
        db.execSQL("INSERT INTO questions(lesson_id,prompt,a,b,c,correct,explanation,final_test) VALUES(?,?,?,?,?,?,?,?)",arrayOf(lessonId,prompt.trim(),options[0].trim(),options[1].trim(),options[2].trim(),correct,explanation.trim(),if(finalTest) 1 else 0))
        audit(actor,"Agregó pregunta ${if(finalTest) "de prueba" else "de práctica"} a unidad $lessonId")
    }
    fun report(actor: Long, studentId: Long, lessonId: Long): Report {
        requireTeacher(actor,studentId)
        val note=rows("SELECT * FROM notes WHERE student_id=? AND lesson_id=?",arrayOf(studentId.toString(),lessonId.toString())) { it.str("note") to (it.num("validated")==1L) }.firstOrNull()
        return Report(getUser(studentId)!!,getLesson(lessonId),attempts(studentId,lessonId),testReview(studentId,lessonId),note?.first ?: "",note?.second ?: false,progress(studentId,lessonId))
    }
    fun saveNote(actor: Long, studentId: Long, lessonId: Long, note: String, validated: Boolean) {
        requireTeacher(actor,studentId)
        require(note.isNotBlank()) { "Escribe una observación pedagógica" }
        db.execSQL("INSERT OR REPLACE INTO notes VALUES(?,?,?,?)",arrayOf(studentId,lessonId,note.trim(),if(validated) 1 else 0))
        audit(actor,"Guardó observación ${if(validated) "validada" else "en borrador"} para $studentId / $lessonId")
    }
    fun updatePhoto(actor: Long, studentId: Long, path: String) { requireTeacher(actor,studentId); db.execSQL("UPDATE users SET photo=? WHERE id=?",arrayOf(path,studentId)); audit(actor,"Actualizó imagen ficticia del estudiante $studentId") }
    fun auditLog(actor: Long): List<AuditEntry> { requireRole(actor,Role.UTP); return rows("SELECT * FROM audit ORDER BY id DESC LIMIT 100") { AuditEntry(it.str("date"),it.str("actor"),it.str("action")) } }
    fun reportText(actor: Long, studentId: Long, lessonId: Long): String {
        val r=report(actor,studentId,lessonId)
        return buildString {
            appendLine("MPAZ PRO 2.º · INFORME PEDAGÓGICO DE DEMOSTRACIÓN")
            appendLine("Datos ficticios. Reservado al docente. Emitido: ${now()}")
            appendLine("${r.student.name} · ${r.student.course}\n${r.lesson.subject}: ${r.lesson.title}\nObjetivo: ${r.lesson.objective}\nAvance: ${r.progress}%")
            r.attempts.forEach { appendLine("Intento ${it.number}: ${it.score}/${it.total}, ${it.date}") }
            r.answers.forEach { (q,a) -> appendLine("${q.prompt}\nRespuesta: ${q.options[a.choice]}. ${if(a.correct) "Correcta" else "Por reforzar"}. ${q.explanation}") }
            appendLine("Observación docente: ${r.note.ifBlank { "Pendiente" }}\nValidación: ${if(r.validated) "Validado" else "Pendiente"}")
            appendLine("No contiene diagnósticos automáticos. Interpretación a cargo del docente.")
        }
    }
}
