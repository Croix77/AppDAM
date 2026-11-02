# Progreso, finalización y navegación

## Qué cambia para el estudiante

1. La práctica muestra una actividad por vez y un encabezado Actividad 1 de 3, 2 de 3, etc. Completadas indica cuántas ya fueron resueltas correctamente.
2. Siguiente actividad se habilita al resolver la actual. Actividad anterior permite revisar. Matemática mantiene su presentación por retos.
3. Al completar todas se muestra Práctica terminada, el total (3/3, 4/4, etc.), confirmación del guardado y botones Volver a la unidad / Revisar actividades.
4. La prueba también avanza por preguntas. Respondidas cuenta las alternativas seleccionadas, sin revelar cuáles son correctas durante el examen. Pregunta anterior permite revisar la elección antes de enviar.
5. Enviar prueba aparece en la última pregunta y exige haber respondido todas. El guardado sigue siendo una transacción del repositorio.
6. Después del envío, el formulario se reemplaza por Prueba terminada. Se muestran preguntas respondidas, aciertos, intento utilizado y fecha. Responder 3/3 no significa acertar 3/3.
7. Volver a la unidad termina el recorrido. Ver último resultado permite consultar un envío guardado sin consumir otro intento. Iniciar otro intento abre un formulario nuevo; solo enviar consume un intento.
8. El botón de navegación superior ahora incluye flecha, palabra Volver, fondo visible y una descripción para accesibilidad. Conserva la navegación de volver a la pantalla anterior.

El estado completo de una práctica se deriva de respuestas SQLite. Las respuestas y los intentos ya existentes no se borran ni se cambian de formato. Al volver a entrar a una práctica ya completada se muestra su confirmación; Revisar actividades abre sus respuestas.

## Código y relación con las guías

- LearningProgress.kt reutiliza Column, Text, Button y LinearProgressIndicator.
- LearningScreens.kt utiliza un índice con rememberSaveable para elegir la pregunta actual. La función key crea el área de la nueva pregunta desde el inicio, evitando arrastrar la posición de desplazamiento de la anterior.
- SchoolViewModel distingue el formulario de la pantalla de resultado mediante testFinished. Esa variable controla solo la presentación; el resultado permanente sigue almacenado en SQLite y se consulta al seleccionar Ver último resultado.
- No se incorporaron librerías nuevas ni se modificó el esquema de datos. Se conservan StateFlow, NavHost y los componentes trabajados en las guías.

Los borradores de selección de examen se mantienen en el ViewModel al usar Pregunta anterior/siguiente. Los borradores locales de práctica sobreviven a la rotación de la pantalla, pero cambiar a otra actividad puede descartarlos; las respuestas correctas guardadas no se pierden.

## Pruebas que respetan el progreso del usuario

Se añadió una opción de Gradle únicamente para verificación: `-PmpazIsolatedTests=true` cambia el applicationId a cl.duoc.m.validation. Así la instalación y retirada de la aplicación de pruebas no afecta la base de cl.duoc.m, usada por el estudiante. Esta configuración de pruebas es un complemento de infraestructura, no una función que deba aprender el usuario de la aplicación.

Comandos:

```powershell
.\gradlew.bat connectedDebugAndroidTest testDebugUnitTest lintDebug -PmpazIsolatedTests=true
.\gradlew.bat assembleDebug
```

El segundo comando produce el APK normal. No distribuir el APK con identificador validation como si fuera una actualización del paquete habitual.

Se comprueba el recorrido completo de práctica, los contadores, el resultado con una respuesta correcta y otra incorrecta, la consulta del último resultado y el inicio de otro intento con elecciones vacías. También se verifica la finalización de los cuatro ejercicios numéricos y su revisión.
