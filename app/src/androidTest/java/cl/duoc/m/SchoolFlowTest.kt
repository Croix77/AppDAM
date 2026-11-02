package cl.duoc.m

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class SchoolFlowTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    private fun login(user: String,password: String) {
        compose.onNodeWithText("Usuario").performTextInput(user)
        compose.onNodeWithText("Clave").performTextInput(password)
        compose.onNodeWithText("Entrar").performScrollTo().performClick()
    }
    @Test fun studentCanOpenAnAssignedUnitAndCannotEditPhotos() {
        login("estudiante","Aprendo123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Bienvenido a MPAZ PRO").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Administrar imagen del estudiante").assertDoesNotExist()
        compose.onNodeWithText("Una visita al huerto").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Lee y descubre").fetchSemanticsNodes().isNotEmpty()}
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
        compose.onNodeWithText("Rojos",substring=true).performScrollTo().performClick()
        compose.onNodeWithText("Agua y luz",substring=true).performScrollTo().performClick()
        compose.onNodeWithText("Enviar prueba").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Revisión del último intento").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Revisión del último intento").performScrollTo()
    }
    @Test fun teacherCanReachPhotoAdministration() {
        login("docente","Profe123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Tu curso").fetchSemanticsNodes().isNotEmpty()}
        compose.onAllNodesWithText("Ver perfil y aprendizaje")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Administrar imagen del estudiante").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Administrar imagen del estudiante").performScrollTo().performClick()
        compose.onNodeWithText("Elegir desde galería").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Tomar foto de prueba").performScrollTo().assertIsEnabled()
    }
    @Test fun utpCanSeeReviewWorkflow() {
        login("utp","Utp12345")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Gestión pedagógica").fetchSemanticsNodes().isNotEmpty()}
        compose.onAllNodesWithText("Gestionar unidad")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Pasar a deshabilitado").fetchSemanticsNodes().isNotEmpty()}
    }
    @Test fun mathAcceptsTypedResultAndLanguageAcceptsOrderedSteps() {
        login("estudiante","Aprendo123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Bienvenido a MPAZ PRO").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Resolvemos en la feria").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("1. Practicar").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("1. Practicar").performScrollTo().performClick()
        compose.onAllNodesWithText("Tu respuesta numérica")[0].performScrollTo().performTextInput("46")
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onAllNodesWithText("Comprobar resultado")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Bien hecho!",substring=true).fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("←").performClick()
        compose.onNodeWithText("←").performClick()
        compose.onNodeWithText("La biblioteca viajera").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("1. Practicar").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("1. Practicar").performScrollTo().performClick()
        listOf("Limpió el estante","Ordenó los libros","Preparó la lista").forEach {compose.onNodeWithText(it).performScrollTo().performClick()}
        compose.onNodeWithText("Comprobar orden").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Bien hecho!",substring=true).fetchSemanticsNodes().isNotEmpty()}
    }
}
