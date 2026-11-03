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
        compose.onNodeWithText("Usuario").performTextClearance()
        compose.onNodeWithText("Usuario").performTextInput(user)
        compose.onNodeWithText("Clave").performTextClearance()
        compose.onNodeWithText("Clave").performTextInput(password)
        compose.onNodeWithText("Entrar").performScrollTo().performClick()
    }
    private fun awaitConfirmation(text: String) {
        compose.waitUntil(10000) {compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()}
        compose.waitUntil(15000) {compose.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()}
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
    @Test fun loginRejectsEmptyAndWrongCredentialsAndAllowsRetry() {
        screenshot("login_redesign")
        compose.onNodeWithText("Estudiante · estudiante / Aprendo123").assertDoesNotExist()
        compose.onNodeWithText("Accesos de prueba").performScrollTo().performClick()
        compose.onNodeWithText("Estudiante · estudiante / Aprendo123").assertExists()
        compose.onNodeWithText("Ocultar accesos de prueba").performScrollTo().performClick()
        compose.onNodeWithText("Entrar").performScrollTo().performClick()
        compose.onNodeWithText("Escribe tu usuario").assertExists()
        compose.onNodeWithText("Escribe tu clave").assertExists()
        login("estudiante","incorrecta")
        compose.waitUntil(10000) {compose.onAllNodesWithText("Usuario o clave incorrectos").fetchSemanticsNodes().isNotEmpty()}
        login("luna","Aprendo123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("¿Qué exploramos?").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithContentDescription("Abrir mi perfil").performClick()
        compose.onNodeWithText("EST-004").assertExists()
    }
    @Test fun allSeedUnitsOpenAndTheLessonSurvivesActivityRecreation() {
        val helper=cl.duoc.m.repository.SchoolDatabase(InstrumentationRegistry.getInstrumentation().targetContext)
        val lessons=try {cl.duoc.m.repository.SchoolRepository(helper).lessons(4).take(8)} finally {helper.close()}
        login("luna","Aprendo123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("¿Qué exploramos?").fetchSemanticsNodes().isNotEmpty()}
        lessons.groupBy {it.subject}.forEach {(subject,units) ->
            compose.onNodeWithText(subject).performScrollTo().performClick()
            units.forEach {lesson ->
                compose.onNodeWithText(lesson.title).performScrollTo().performClick()
                compose.waitUntil(10000) {compose.onAllNodesWithText("Lee y descubre").fetchSemanticsNodes().isNotEmpty()}
                compose.onNodeWithText(lesson.explanation).performScrollTo().assertExists()
                if(lesson.id==1L) {
                    compose.onNodeWithText("Escuchar instrucciones").performScrollTo().performClick()
                    compose.onNodeWithText("Detener instrucciones de audio").performScrollTo().performClick()
                    compose.activityRule.scenario.recreate()
                    compose.onNodeWithText("Lee y descubre").assertExists()
                    compose.onNodeWithText(lesson.title).assertExists()
                }
                compose.onNodeWithText("1. Practicar").performScrollTo().assertIsEnabled()
                compose.onNodeWithText("2. Prueba final",substring=true).performScrollTo().assertIsNotEnabled()
                compose.onNodeWithText("Volver").performClick()
            }
            compose.onNodeWithText("Volver").performClick()
        }
    }
    @Test fun teacherCanValidateAndPersistAnObservation() {
        login("docente","Profe123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Mis cursos").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("2.º Básico A").performScrollTo().performClick()
        compose.onAllNodesWithText("Ver perfil y aprendizaje")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Consultar informe pedagógico").fetchSemanticsNodes().isNotEmpty()}
        compose.onAllNodesWithText("Máximo de intentos (1 a 5)")[0].performScrollTo().performTextClearance()
        compose.onAllNodesWithText("Máximo de intentos (1 a 5)")[0].performTextInput("0")
        compose.onAllNodesWithText("Guardar asignación")[0].performScrollTo().performClick()
        compose.onNodeWithText("Escribe un número entre 1 y 5").assertExists()
        compose.onAllNodesWithText("Consultar informe pedagógico")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Guardar observación").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Fortalezas y aspectos a reforzar").performScrollTo().performTextClearance()
        compose.onNodeWithText("Guardar observación").performScrollTo().performClick()
        compose.onNodeWithText("Escribe una observación pedagógica").assertExists()
        compose.onNodeWithText("Fortalezas y aspectos a reforzar").performScrollTo().performTextInput("Reforzar comprensión de instrucciones. Prueba automática.")
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNode(isToggleable()).performScrollTo().performClick()
        compose.onNode(isToggleable()).assertIsOn()
        compose.onNodeWithText("Guardar observación").performScrollTo().performClick()
        screenshot("report_after_save")
        compose.onNodeWithText("Estado guardado:",substring=true).performScrollTo()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Estado guardado: validado").fetchSemanticsNodes().isNotEmpty()}
        awaitConfirmation("Observación guardada")
        compose.onNodeWithText("Volver").performClick()
        compose.onAllNodesWithText("Consultar informe pedagógico")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Estado guardado: validado").fetchSemanticsNodes().isNotEmpty()}
    }
    @Test fun utpCanCreateQuestionsAndPublishThenDisableAUnit() {
        login("utp","Utp12345")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Crear unidad").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Crear unidad").performScrollTo().performClick()
        compose.onNodeWithText("Guardar borrador").performScrollTo().performClick()
        compose.onAllNodesWithText("Completa este campo").assertCountEquals(5)
        listOf("Título" to "Unidad de validación", "Objetivo de aprendizaje (descripción)" to "Identificar una respuesta.","Explicación escrita" to "Leemos las instrucciones.","Ejemplo guiado" to "La alternativa correcta es A.","Autoría y permiso de uso" to "Material ficticio original de prueba.").forEach {(label,value) -> compose.onNodeWithText(label).performScrollTo().performTextInput(value)}
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Guardar borrador").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Agregar pregunta al borrador").fetchSemanticsNodes().isNotEmpty()}
        awaitConfirmation("Borrador guardado")
        listOf(false,true).forEach {finalTest ->
            listOf("Pregunta" to "¿Cuál alternativa es correcta?", "Alternativa A" to "Primera", "Alternativa B" to "Segunda", "Alternativa C" to "Tercera", "Explicación de la respuesta" to "La primera es correcta.").forEach {(label,value) -> compose.onNodeWithText(label).performScrollTo().performTextInput(value)}
            androidx.test.espresso.Espresso.closeSoftKeyboard()
            if(finalTest) compose.onNode(isToggleable()).performScrollTo().performClick()
            compose.onNodeWithText("Agregar pregunta al borrador").performScrollTo().performClick()
            awaitConfirmation("Pregunta agregada")
            val count=if(finalTest) "Preguntas de práctica (1) y prueba (1)" else "Preguntas de práctica (1) y prueba (0)"
            compose.onNodeWithText("Preguntas de práctica (",substring=true).performScrollTo()
            compose.waitUntil(10000) {compose.onAllNodesWithText(count).fetchSemanticsNodes().isNotEmpty()}
        }
        listOf("en revision","autorizado","publicado","deshabilitado").forEach {status ->
            compose.onNodeWithText("Pasar a $status").performScrollTo().performClick()
            compose.onNodeWithText("Estado:",substring=true).performScrollTo()
            compose.waitUntil(10000) {compose.onAllNodesWithText("Estado: ${status.uppercase().replace('Ó','O')}").fetchSemanticsNodes().isNotEmpty()}
        }
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Consultar historial de cambios").performScrollTo().performClick()
        compose.onNodeWithText("Historial UTP").assertExists()
    }
    @Test fun studentCanOpenAnAssignedUnitAndCannotEditPhotos() {
        login("estudiante","Aprendo123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("¿Qué exploramos?").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Administrar imagen del estudiante").assertDoesNotExist()
        screenshot("home_student")
        compose.onNodeWithText("Lenguaje").performScrollTo().performClick()
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
        compose.waitUntil(15000) {compose.onAllNodesWithText("Mis cursos").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("2.º Básico A").performScrollTo().performClick()
        compose.onAllNodesWithText("Ver perfil y aprendizaje")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Administrar imagen del estudiante").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Administrar imagen del estudiante").performScrollTo().performClick()
        compose.onNodeWithText("Elegir desde galería").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Tomar foto").performScrollTo().assertIsEnabled()
        screenshot("photo_teacher")
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Mi perfil").performClick()
        compose.onNodeWithText("Docente de prueba").assertExists()
        compose.onNodeWithText("Cursos a cargo").assertExists()
        compose.onNodeWithText("Estudiante Luna").assertDoesNotExist()
        screenshot("profile_teacher")
    }
    @Test fun cancellingCameraAndGalleryReturnsWithoutChangingTheAvatar() {
        login("docente","Profe123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Mis cursos").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("2.º Básico A").performScrollTo().performClick()
        compose.onAllNodesWithText("Ver perfil y aprendizaje")[0].performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Administrar imagen del estudiante").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Administrar imagen del estudiante").performScrollTo().performClick()
        val instrumentation=InstrumentationRegistry.getInstrumentation()
        val automation=instrumentation.uiAutomation
        listOf("Tomar foto","Elegir desde galería").forEach {label ->
            compose.onNodeWithText(label).performScrollTo().performClick()
            compose.waitUntil(10000) {automation.rootInActiveWindow?.packageName?.toString()?.let {it!=instrumentation.targetContext.packageName}==true}
            automation.executeShellCommand("input keyevent KEYCODE_BACK").close()
            compose.waitUntil(10000) {automation.rootInActiveWindow?.packageName?.toString()==instrumentation.targetContext.packageName}
            compose.onNodeWithText("Imagen actual").assertExists()
            compose.onNodeWithText("Guardar esta imagen").assertDoesNotExist()
        }
    }
    @Test fun newHomeKeepsReadingSettingsAndLogoutAccessible() {
        login("estudiante","Aprendo123")
        compose.waitUntil(15000) {compose.onAllNodesWithText("¿Qué exploramos?").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithContentDescription("Abrir mi perfil").performClick()
        compose.onNodeWithText("Mi perfil").assertExists()
        compose.onNodeWithText("EST-003").assertExists()
        compose.onNodeWithText("2.º Básico A").assertExists()
        compose.onNodeWithText("Docente de prueba").assertExists()
        compose.onNodeWithText("Administrar imagen del estudiante").assertDoesNotExist()
        compose.onNodeWithText("Consultar informe pedagógico").assertDoesNotExist()
        screenshot("profile_student")
        compose.onNodeWithText("Aumentar texto").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Reducir texto").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Reducir texto").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Aumentar texto").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Salir").performScrollTo().assertIsEnabled().performClick()
        compose.onNodeWithText("Entrar").assertExists()
    }
    @Test fun utpCanOpenItsOwnAccountProfile() {
        login("utp","Utp12345")
        compose.waitUntil(15000) {compose.onAllNodesWithText("Gestión pedagógica").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Mi perfil").performClick()
        compose.onNodeWithText("Coordinación UTP").assertExists()
        compose.onNodeWithText("CTA-001").assertExists()
        compose.onNodeWithText("Área de trabajo").assertExists()
        compose.onNodeWithText("Consultar informe pedagógico").assertDoesNotExist()
        screenshot("profile_utp")
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
        compose.waitUntil(15000) {compose.onAllNodesWithText("¿Qué exploramos?").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Matemática").performScrollTo().performClick()
        compose.onNodeWithText("Una visita al huerto").assertDoesNotExist()
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
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Lenguaje").performScrollTo().performClick()
        compose.onNodeWithText("La biblioteca viajera").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("1. Practicar").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("1. Practicar").performScrollTo().performClick()
        listOf("Limpió el estante","Ordenó los libros","Preparó la lista").forEach {compose.onNodeWithText(it).performScrollTo().performClick()}
        compose.onNodeWithText("Comprobar orden").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("¡Bien hecho!",substring=true).fetchSemanticsNodes().isNotEmpty()}
        screenshot("language_order")
    }
}
