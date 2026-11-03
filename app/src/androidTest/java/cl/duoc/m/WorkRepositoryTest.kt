package cl.duoc.m

import androidx.test.platform.app.InstrumentationRegistry
import cl.duoc.m.model.*
import cl.duoc.m.repository.*
import org.junit.*
import org.junit.Assert.*
import java.time.LocalDate

class WorkRepositoryTest {
    private val context=InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var helper: SchoolDatabase
    private lateinit var works: WorkRepository
    private lateinit var school: SchoolRepository
    @Before fun setup() {context.deleteDatabase("works_test.db");helper=SchoolDatabase(context,"works_test.db");works=WorkRepository(helper);school=SchoolRepository(helper)}
    @After fun cleanup() {helper.close();context.deleteDatabase("works_test.db")}
    private fun denied(action: ()->Unit) {try {action();fail("Debía rechazarse")} catch(_: IllegalArgumentException) {}}
    private fun create()=works.create(2,"2.º Básico A","Lenguaje","Mi lugar favorito","Describe un lugar y explica por qué te gusta.",LocalDate.now().plusDays(7).toString())

    @Test fun workIsAssignedOnlyToItsCourseAndPrivateDraftsStayPrivate() {
        val id=create()
        val mateo=school.login("mateo","Aprendo123")!!
        assertEquals(2,works.work(2,id).assigned)
        assertEquals(id,works.works(3).single().id)
        assertEquals(id,works.works(4).single().id)
        assertTrue(works.works(mateo.id).isEmpty())
        denied {works.work(mateo.id,id)}
        denied {works.submission(4,id,3)}
        denied {works.create(3,"2.º Básico A","Lenguaje","Título","Texto",LocalDate.now().toString())}
        denied {works.works(1)}
        works.saveResponse(3,id,"Borrador privado del estudiante.",false)
        assertEquals("Pendiente",works.submission(3,id,3)!!.status)
        assertNull(works.submission(2,id,3))
        assertEquals(0,works.work(2,id).delivered)
        denied {works.saveReview(2,id,3,"6,0","No hay entrega",true)}
    }
    @Test fun deliveredResponseAndPublishedGradeSurviveReopeningAndCannotBeOverwritten() {
        val id=create()
        works.saveResponse(3,id,"Mi lugar favorito es la biblioteca.",true)
        denied {works.saveResponse(3,id,"Otra respuesta",true)}
        works.saveReview(2,id,3,"6,5","Comentario provisional",false)
        assertNull(works.submission(3,id,3)!!.gradeTenths)
        assertEquals("",works.submission(3,id,3)!!.feedback)
        assertEquals(65,works.submission(2,id,3)!!.gradeTenths)
        denied {works.saveReview(2,id,3,"7,1","Fuera de rango",true)}
        denied {works.saveReview(2,id,3,"6,5"," ",true)}
        works.saveReview(2,id,3,"6,5","Describes bien el lugar. Revisa las mayúsculas.",true)
        denied {works.saveReview(2,id,3,"1,0","Reemplazo",false)}
        helper.close();helper=SchoolDatabase(context,"works_test.db");works=WorkRepository(helper)
        val result=works.submission(3,id,3)!!
        assertEquals("Revisado",result.status)
        assertEquals("6,5",result.gradeLabel)
        assertTrue(result.feedback.contains("mayúsculas"))
        assertEquals(1,works.work(2,id).delivered)
        assertEquals(1,works.work(2,id).reviewed)
    }
    @Test fun deadlinesAndForeignTeachersCannotChangeSubmissions() {
        val id=create()
        denied {works.saveResponse(3,id," ",true)}
        helper.writableDatabase.execSQL("UPDATE school_work SET due_date='2000-01-01' WHERE id=?",arrayOf(id))
        denied {works.saveResponse(3,id,"Fuera de plazo",true)}
        helper.writableDatabase.execSQL("UPDATE school_work SET due_date=? WHERE id=?",arrayOf(LocalDate.now().toString(),id))
        works.saveResponse(3,id,"Entregado el último día",true)
        helper.writableDatabase.execSQL("UPDATE users SET role='DOCENTE' WHERE id=1")
        denied {works.work(1,id)}
        denied {works.saveReview(1,id,3,"6,0","Sin permiso",true)}
        denied {works.saveReview(4,id,3,"6,0","Sin permiso",true)}
        assertNull(works.submission(3,id,3)!!.gradeTenths)
    }
    @Test fun migrationFromV2KeepsOldLearningDataAndPhotos() {
        school.questions(1,false).forEach {school.savePractice(3,1,it.id,it.correct)}
        school.submitTest(3,1,school.questions(1,true).associate {it.id to it.correct})
        school.saveNote(2,3,1,"Observación anterior",true)
        school.updatePhoto(2,3,"avatar-anterior.jpg")
        val mateo=school.login("mateo","Aprendo123")!!
        val db=helper.writableDatabase
        db.execSQL("DROP TABLE work_submissions");db.execSQL("DROP TABLE work_students");db.execSQL("DROP TABLE school_work")
        db.execSQL("DELETE FROM assignments WHERE student_id=?",arrayOf(mateo.id));db.execSQL("DELETE FROM users WHERE id=?",arrayOf(mateo.id))
        db.version=2;helper.close()
        helper=SchoolDatabase(context,"works_test.db");school=SchoolRepository(helper);works=WorkRepository(helper)
        assertEquals(100,school.progress(3,1))
        assertEquals(1,school.attempts(3,1).size)
        assertEquals(2,school.testReview(3,1).size)
        assertEquals("Observación anterior",school.report(2,3,1).note)
        assertEquals("avatar-anterior.jpg",school.getUser(3)!!.photo)
        assertTrue(works.works(3).isEmpty())
        assertEquals(3,helper.readableDatabase.version)
        assertNotNull(school.login("mateo","Aprendo123"))
    }
}
