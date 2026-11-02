package cl.duoc.m

import androidx.test.platform.app.InstrumentationRegistry
import cl.duoc.m.model.*
import cl.duoc.m.repository.*
import org.junit.*
import org.junit.Assert.*

class SchoolRepositoryTest {
    private val context=InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var helper: SchoolDatabase
    private lateinit var repo: SchoolRepository
    @Before fun setup() {context.deleteDatabase("school_test.db");helper=SchoolDatabase(context,"school_test.db");repo=SchoolRepository(helper)}
    @After fun cleanup() {helper.close();context.deleteDatabase("school_test.db")}
    private fun forbidden(action: ()->Unit) {try {action();fail("La operación debía ser rechazada")} catch(_: IllegalArgumentException) {}}
    @Test fun fictitiousAccountsAndFourSubjects() {
        assertEquals(Role.ESTUDIANTE,repo.login("estudiante","Aprendo123")!!.role)
        assertNull(repo.login("estudiante","incorrecta"))
        assertNull(repo.login("' OR 1=1 --","Aprendo123"))
        assertEquals(4,repo.lessons(3).map {it.subject}.distinct().size)
        assertEquals(8,repo.lessons(3).size)
        assertEquals(2,repo.students(2).size)
    }
    @Test fun accessControlProtectsReportsAssignmentsAndPhotos() {
        forbidden {repo.report(3,3,1)}
        forbidden {repo.updatePhoto(3,3,"fake.jpg")}
        forbidden {repo.assign(3,3,1,2,"2026-01-01","2030-01-01")}
        forbidden {repo.setStatus(2,1,UnitStatus.DESHABILITADO)}
        repo.updatePhoto(2,3,"test-avatar.jpg")
        assertEquals("test-avatar.jpg",repo.getUser(3)!!.photo)
    }
    @Test fun attemptLimitAndAnswersSurviveDatabaseReopening() {
        repo.questions(1,false).forEach {repo.savePractice(3,1,it.id,it.correct)}
        val choices=repo.questions(1,true).associate {it.id to it.correct}
        assertEquals(2,repo.submitTest(3,1,choices).score)
        assertEquals(2,repo.submitTest(3,1,choices).number)
        forbidden {repo.submitTest(3,1,choices)}
        helper.close();helper=SchoolDatabase(context,"school_test.db");repo=SchoolRepository(helper)
        assertEquals(2,repo.attempts(3,1).size)
        assertEquals(100,repo.progress(3,1))
        assertEquals(2,repo.testReview(3,1).size)
        assertTrue(repo.report(2,3,1).answers.all {it.second.correct})
    }
    @Test fun publicationRequiresReviewAndQuestions() {
        val id=repo.saveLesson(1,null,"Lenguaje","Nueva unidad","Objetivo","Explicación","Ejemplo","Autoría propia")
        assertEquals(UnitStatus.BORRADOR,repo.getLesson(id).status)
        forbidden {repo.setStatus(1,id,UnitStatus.PUBLICADO)}
        repo.setStatus(1,id,UnitStatus.EN_REVISION)
        forbidden {repo.setStatus(1,id,UnitStatus.AUTORIZADO)}
        repo.setStatus(1,id,UnitStatus.BORRADOR)
        listOf(false,true).forEach {repo.saveQuestion(1,id,"¿Cuál?",listOf("A","B","C"),0,"Respuesta A",it)}
        repo.setStatus(1,id,UnitStatus.EN_REVISION);repo.setStatus(1,id,UnitStatus.AUTORIZADO)
        repo.assign(2,3,id,1,"2026-01-01","2030-01-01")
        assertFalse(repo.lessons(3).any {it.id==id})
        repo.setStatus(1,id,UnitStatus.PUBLICADO)
        assertTrue(repo.lessons(3).any {it.id==id})
        repo.setStatus(1,id,UnitStatus.DESHABILITADO)
        assertFalse(repo.lessons(3).any {it.id==id})
    }
    @Test fun teacherNotesAndExportArePersisted() {
        repo.saveNote(2,3,1,"Reforzar lectura de instrucciones",true)
        val report=repo.report(2,3,1)
        assertTrue(report.validated)
        assertTrue(repo.reportText(2,3,1).contains("Reforzar lectura"))
        forbidden {repo.reportText(3,3,1)}
    }
    @Test fun upgradedDatabasePreservesEarlierAnswersAndAttempts() {
        val name="upgrade_test.db"
        context.deleteDatabase(name)
        val legacy=android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(name),null)
        try {
            val testContext=InstrumentationRegistry.getInstrumentation().context
            testContext.assets.open("legacy_v1.sql").bufferedReader().use {it.readText()}.split(';').filter {it.isNotBlank()}.forEach {legacy.execSQL(it)}
            listOf(1,2,3,4).forEach {id -> legacy.execSQL("INSERT INTO users(id,username,name,role,course,teacher_id,salt,password) VALUES(?,?,?,?,?,?,?,?)",arrayOf(id,"test$id","Test $id",if(id<3) "DOCENTE" else "ESTUDIANTE","2A",if(id>2) 2 else null,"salt","hash"))}
            SeedContent.insert(legacy)
            legacy.execSQL("INSERT INTO practice VALUES(3,1,0,1,'2026-01-01 10:00:00')")
            legacy.execSQL("INSERT INTO attempts VALUES(1,3,1,1,2,2,'2026-01-01 10:01:00')")
            legacy.execSQL("INSERT INTO test_answers VALUES(1,3,2,1)")
            legacy.version=1
        } finally {legacy.close()}
        val upgraded=SchoolDatabase(context,name)
        try {
            val migrated=SchoolRepository(upgraded)
            assertEquals(1,migrated.attempts(3,1).size)
            assertEquals(1,migrated.practice(3,1).size)
            assertEquals(8,migrated.lessons(3).size)
            val math=migrated.lessons(3).first {it.title=="Resolvemos en la feria"}
            assertEquals(4,migrated.questions(math.id,false).count {it.kind==ActivityKind.NUMERO})
        } finally {upgraded.close();context.deleteDatabase(name)}
    }
    @Test fun closedDatesRejectPracticeAndIncompleteTestsDoNotConsumeAttempts() {
        repo.assign(2,3,1,2,"2020-01-01","2020-12-31")
        assertNotNull(repo.available(3,1))
        try {repo.savePractice(3,1,1,0);fail()} catch(_: IllegalStateException) {}
        repo.assign(2,3,1,2,"2026-01-01","2030-12-31")
        forbidden {repo.submitTest(3,1,emptyMap())}
        assertTrue(repo.attempts(3,1).isEmpty())
    }
}
