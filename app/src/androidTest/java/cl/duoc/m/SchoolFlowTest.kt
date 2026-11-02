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
        compose.onNodeWithText("En el huerto").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Bien hecho!",substring=true).fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Regó una lechuga").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Bien hecho!",substring=true).fetchSemanticsNodes().size==2}
        compose.onNodeWithText("←").performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Tu avance: 66%").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("2. Prueba final",substring=true).performScrollTo().assertIsEnabled().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Comprueba lo aprendido").fetchSemanticsNodes().isNotEmpty()}
        compose.waitForIdle()
        screenshot("test_before")
        compose.onNodeWithText("Rojos",substring=true).performScrollTo().performClick()
        compose.onNodeWithText("Agua y luz",substring=true).performScrollTo().performClick()
        compose.onNodeWithText("Enviar prueba").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Revisión del último intento").fetchSemanticsNodes().isNotEmpty()}
        screenshot("test_result")
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
}
