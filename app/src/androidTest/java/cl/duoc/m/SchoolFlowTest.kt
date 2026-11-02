package cl.duoc.m

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

class SchoolFlowTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    private fun login(user: String,password: String) {
        compose.onNodeWithText("Usuario").performTextInput(user)
        compose.onNodeWithText("Clave").performTextInput(password)
        compose.onNodeWithText("Entrar").performScrollTo().performClick()
    }
    private fun screenshot(name: String) {
        compose.mainClock.advanceTimeBy(500)
        compose.waitForIdle()
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val values=android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME,"$name.png")
            put(android.provider.MediaStore.Images.Media.MIME_TYPE,"image/png")
            put(android.provider.MediaStore.Images.Media.RELATIVE_PATH,"Pictures/MPAZTest")
        }
        val uri=requireNotNull(context.contentResolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values))
        context.contentResolver.openOutputStream(uri)!!.use {compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
    }
    @Test fun studentCanOpenAnAssignedUnitAndCannotEditPhotos() {
        login("estudiante","Aprendo123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Bienvenido a MPAZ PRO").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Administrar imagen del estudiante").assertDoesNotExist()
        screenshot("home_student")
        compose.onNodeWithText("Una visita al huerto").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Lee y descubre").fetchSemanticsNodes().isNotEmpty()}
        screenshot("lesson")
        compose.onNodeWithText("1. Practicar").performScrollTo().performClick()
        compose.onNodeWithText("Actividad 1 de 2").assertExists()
        compose.onNodeWithText("En el huerto").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Bien hecho!",substring=true).fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Siguiente actividad").performScrollTo().performClick()
        compose.onNodeWithText("Actividad 2 de 2").assertExists()
        compose.onNodeWithText("Regó una lechuga").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Práctica terminada!").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("✓ 2/2 actividades completadas").assertExists()
        screenshot("practice_completed")
        compose.onNodeWithText("Volver a la unidad").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Tu avance: 66%").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("2. Prueba final",substring=true).performScrollTo().assertIsEnabled().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Comprueba lo aprendido").fetchSemanticsNodes().isNotEmpty()}
        compose.waitForIdle()
        screenshot("test_before")
        compose.onNodeWithText("Pregunta 1 de 2").assertExists()
        compose.onNodeWithText("Azules",substring=true).performScrollTo().performClick()
        compose.onNodeWithText("Siguiente pregunta").performScrollTo().performClick()
        compose.onNodeWithText("Pregunta 2 de 2").assertExists()
        compose.onNodeWithText("Agua y luz",substring=true).performScrollTo().performClick()
        compose.onNodeWithText("Enviar prueba").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Prueba terminada!").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("✓ Preguntas respondidas: 2/2").assertExists()
        compose.onNodeWithText("Respuestas correctas: 1/2").assertExists()
        compose.onNodeWithText("Enviar prueba").assertDoesNotExist()
        screenshot("test_completed")
        compose.waitUntil(10000) {compose.onAllNodesWithText("Revisión del último intento").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Revisión del último intento").performScrollTo()
        screenshot("test_result")
        compose.onNodeWithText("Volver a la unidad").performScrollTo().performClick()
        compose.onNodeWithText("Ver último resultado").performScrollTo().performClick()
        compose.onNodeWithText("Respuestas correctas: 1/2").assertExists()
        compose.onNodeWithText("Iniciar otro intento").performScrollTo().performClick()
        compose.onNodeWithText("Pregunta 1 de 2").assertExists()
        compose.onNodeWithText("Respondidas: 0/2").assertExists()
    }
    @Test fun teacherCanReachPhotoAdministration() {
        login("docente","Profe123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Tu curso").fetchSemanticsNodes().isNotEmpty()}
        compose.onAllNodesWithText("Ver perfil y aprendizaje")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Administrar imagen del estudiante").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Administrar imagen del estudiante").performScrollTo().performClick()
        compose.onNodeWithText("Elegir desde galería").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Tomar foto de prueba").performScrollTo().assertIsEnabled()
        screenshot("photo_teacher")
    }
    @Test fun utpCanSeeReviewWorkflow() {
        login("utp","Utp12345")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Gestión pedagógica").fetchSemanticsNodes().isNotEmpty()}
        screenshot("utp")
        compose.onAllNodesWithText("Gestionar unidad")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Pasar a deshabilitado").fetchSemanticsNodes().isNotEmpty()}
        screenshot("utp_unit")
    }
    @Test fun mathAcceptsTypedResultAndLanguageAcceptsOrderedSteps() {
        login("estudiante","Aprendo123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Bienvenido a MPAZ PRO").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Matemática").performScrollTo().performClick()
        compose.onNodeWithText("Una visita al huerto").assertDoesNotExist()
        compose.onNodeWithText("Ver todas").performScrollTo().performClick()
        compose.onNodeWithText("Una visita al huerto").assertExists()
        compose.onNodeWithText("Resolvemos en la feria").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("1. Practicar").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("1. Practicar").performScrollTo().performClick()
        compose.onNodeWithText("Reto 1 de 4").assertExists()
        screenshot("math_redesign_empty")
        compose.onAllNodesWithText("Tu respuesta numérica")[0].performScrollTo().performTextInput("45")
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Comprobar resultado").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Revisa tu respuesta.",substring=true).fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Siguiente reto →").assertIsNotEnabled()
        compose.onNodeWithText("Tu respuesta numérica").performTextClearance()
        compose.onAllNodesWithText("Tu respuesta numérica")[0].performScrollTo().performTextInput("46")
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onAllNodesWithText("Comprobar resultado")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Bien hecho!",substring=true).fetchSemanticsNodes().isNotEmpty()}
        screenshot("math_numeric")
        compose.onNodeWithText("Siguiente reto →").performScrollTo().performClick()
        compose.onNodeWithText("Reto 2 de 4").assertExists()
        compose.onNodeWithText("← Anterior").performScrollTo().performClick()
        compose.onNodeWithText("Reto 1 de 4").assertExists()
        compose.onNodeWithText("46").assertExists()
        listOf("39","32","35").forEach {result ->
            compose.onNodeWithText("Siguiente reto →").performScrollTo().performClick()
            compose.onNodeWithText("Tu respuesta numérica").performScrollTo().performTextInput(result)
            androidx.test.espresso.Espresso.closeSoftKeyboard()
            compose.onNodeWithText("Comprobar resultado").performScrollTo().performClick()
            compose.waitUntil(10000) {compose.onAllNodesWithText("¡Bien hecho!",substring=true).fetchSemanticsNodes().isNotEmpty() || compose.onAllNodesWithText("¡Práctica terminada!").fetchSemanticsNodes().isNotEmpty()}
        }
        compose.onNodeWithText("✓ 4/4 actividades completadas").assertExists()
        screenshot("math_completed")
        compose.onNodeWithText("Revisar actividades").performScrollTo().performClick()
        compose.onNodeWithText("Reto 1 de 4").assertExists()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("La biblioteca viajera").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("1. Practicar").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("1. Practicar").performScrollTo().performClick()
        listOf("Limpió el estante","Ordenó los libros","Preparó la lista").forEach {compose.onNodeWithText(it).performScrollTo().performClick()}
        compose.onNodeWithText("Comprobar orden").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Bien hecho!",substring=true).fetchSemanticsNodes().isNotEmpty()}
        screenshot("language_order")
    }
}
