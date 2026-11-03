package cl.duoc.m

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import cl.duoc.m.repository.*
import org.junit.*
import org.junit.Assert.*
import java.time.LocalDate

class WorkFlowTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext
    @Before fun cleanWorkFixtures() {
        check(context.packageName.endsWith(".validation")) {"Usa mpazIsolatedTests=true para proteger los datos habituales"}
        SchoolDatabase(context).use {helper ->
            helper.writableDatabase.apply {delete("work_submissions",null,null);delete("work_students",null,null);delete("school_work",null,null)}
        }
    }
    private fun waitText(text: String) {compose.waitUntil(15000) {compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()}}
    private fun login(user: String,password: String) {
        compose.onNodeWithText("Usuario").performScrollTo().performTextInput(user)
        compose.onNodeWithText("Clave").performScrollTo().performTextInput(password)
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Entrar").performScrollTo().performClick()
        waitText(if(user=="docente") "Mis cursos" else "¿Qué exploramos?")
    }
    private fun logout() {compose.onNodeWithText("Salir").performClick();waitText("Entrar")}
    private fun fill(label: String,value: String) {
        compose.onNodeWithText(label).performScrollTo().performTextClearance()
        compose.onNodeWithText(label).performTextInput(value)
        androidx.test.espresso.Espresso.closeSoftKeyboard()
    }
    private fun dismissConfirmation(text: String) {
        waitText(text)
        val dismiss=SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss)
        if(compose.onAllNodes(dismiss).fetchSemanticsNodes().isNotEmpty()) compose.onNode(dismiss).performSemanticsAction(SemanticsActions.Dismiss) {it()}
        compose.waitForIdle()
    }
    private fun openTeacherWork() {
        compose.onNodeWithText("2.º Básico A").performScrollTo().performClick()
        compose.onNodeWithText("Trabajos").performClick()
        compose.onNodeWithText("Mi lugar favorito").performScrollTo().performClick()
        waitText("Entregas del curso")
    }
    private fun openStudentWork() {
        compose.onNodeWithText("Mis trabajos").performScrollTo().performClick()
        compose.onNodeWithText("Mi lugar favorito").performScrollTo().performClick()
        waitText("Mi trabajo")
    }
    private fun shot(name: String) {
        compose.waitForIdle()
        val values=android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME,"$name.png")
            put(android.provider.MediaStore.Images.Media.MIME_TYPE,"image/png")
            put(android.provider.MediaStore.Images.Media.RELATIVE_PATH,"Pictures/ExploraWorkTest")
        }
        val uri=requireNotNull(context.contentResolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values))
        context.contentResolver.openOutputStream(uri)!!.use {compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
    }
    @Test fun teacherAssignsStudentDeliversAndSeesOnlyThePublishedEvaluation() {
        login("docente","Profe123")
        compose.onNodeWithText("2.º Básico B").assertExists()
        shot("courses")
        compose.onNodeWithText("2.º Básico A").performClick()
        compose.onNodeWithText("Trabajos").performClick()
        compose.onNodeWithText("Crear trabajo").performClick()
        compose.onNodeWithText("Asignar trabajo al curso").performScrollTo().performClick()
        compose.onNodeWithText("Escribe un título").assertExists()
        compose.onNodeWithText("Escribe las instrucciones").assertExists()
        fill("Título del trabajo","Mi lugar favorito")
        fill("Instrucciones","Describe tu lugar favorito y explica por qué te gusta. Escribe al menos tres oraciones.")
        shot("work_editor")
        compose.onNodeWithText("Asignar trabajo al curso").performScrollTo().performClick()
        waitText("Entregas del curso")
        dismissConfirmation("Trabajo creado")
        compose.onNodeWithText("Estudiante Mateo").assertDoesNotExist()
        shot("work_deliveries")
        logout()

        login("estudiante","Aprendo123");openStudentWork()
        compose.onNodeWithText("Entregar trabajo").performScrollTo().performClick()
        compose.onNodeWithText("Escribe tu respuesta").assertExists()
        fill("Escribe tu respuesta","Mi lugar favorito es la biblioteca. Allí encuentro cuentos de animales. Me gusta leer con mis amigos.")
        compose.onNodeWithText("Guardar borrador").performScrollTo().performClick()
        dismissConfirmation("Borrador guardado")
        logout();login("estudiante","Aprendo123");openStudentWork()
        compose.onNodeWithText("Mi lugar favorito es la biblioteca. Allí encuentro cuentos de animales. Me gusta leer con mis amigos.").assertExists()
        shot("work_student_draft")
        compose.onNodeWithText("Entregar trabajo").performScrollTo().performClick()
        compose.onNodeWithText("Entregar").performClick()
        waitText("Entregado");dismissConfirmation("Trabajo entregado")
        compose.onNodeWithText("Guardar borrador").assertDoesNotExist()
        logout()

        login("docente","Profe123");openTeacherWork()
        compose.onNodeWithText("Revisar entrega").performScrollTo().performClick()
        waitText("Nota (1,0 a 7,0)")
        fill("Nota (1,0 a 7,0)","7,5")
        compose.onNodeWithText("Publicar evaluación").performScrollTo().performClick()
        compose.onNodeWithText("Escribe una nota entre 1,0 y 7,0, con hasta un decimal").assertExists()
        fill("Nota (1,0 a 7,0)","6,5")
        fill("Comentario para el estudiante","Describes bien el lugar y explicas tu elección. Revisa las mayúsculas al comenzar cada oración.")
        compose.onNodeWithText("Guardar corrección").performScrollTo().performClick()
        dismissConfirmation("Corrección guardada")
        SchoolDatabase(context).use {helper ->
            val repo=WorkRepository(helper);val id=repo.works(3).single().id
            assertNull(repo.submission(3,id,3)!!.gradeTenths)
            assertEquals("",repo.submission(3,id,3)!!.feedback)
        }
        shot("work_teacher_review")
        logout();login("estudiante","Aprendo123");openStudentWork()
        compose.onNodeWithText("Entregado").assertExists()
        compose.onNodeWithText("Nota 6,5").assertDoesNotExist()
        logout();login("docente","Profe123");openTeacherWork()
        compose.onNodeWithText("Revisar entrega").performScrollTo().performClick()
        waitText("Nota (1,0 a 7,0)")
        compose.onNodeWithText("Publicar evaluación").performScrollTo().performClick()
        compose.onNodeWithText("Publicar").performClick()
        waitText("Evaluación publicada");dismissConfirmation("Evaluación publicada")
        logout();login("estudiante","Aprendo123");openStudentWork()
        compose.onNodeWithText("Nota 6,5").performScrollTo().assertExists()
        compose.onNodeWithText("Comentario del docente").assertExists()
        shot("work_student_grade")
    }
    @Test fun teacherCoursesAndStudentWorkListsStaySeparated() {
        SchoolDatabase(context).use {helper -> WorkRepository(helper).create(2,"2.º Básico B","Ciencias Naturales","Observamos una planta","Describe sus hojas.",LocalDate.now().plusDays(5).toString())}
        login("docente","Profe123")
        compose.onNodeWithText("2.º Básico B").performClick()
        compose.onNodeWithText("Estudiante Mateo").assertExists()
        compose.onNodeWithText("Estudiante Sol").assertDoesNotExist()
        compose.onNodeWithText("Trabajos").performClick()
        compose.onNodeWithText("Observamos una planta").assertExists()
        logout();login("estudiante","Aprendo123")
        compose.onNodeWithText("Mis trabajos").performScrollTo().performClick()
        compose.onNodeWithText("Observamos una planta").assertDoesNotExist()
        compose.onNodeWithText("Todo al día").assertExists()
    }
}
