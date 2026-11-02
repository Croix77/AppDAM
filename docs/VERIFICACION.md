# Verificación del MVP

Fecha: 2 de noviembre de 2026.

Entorno usado:

- Windows 11.
- Android Studio 2026.2.2.8.
- JDK 17 Temurin para compilar. El JBR 25 que trae Android Studio falla con esta versión de AGP; hay que dejar el Gradle JDK en 17.
- Android SDK 36.
- Emulador `Medium_Phone_API_37.0` (API 37, Android 17).
- `minSdk 26`, `targetSdk 36`.

## Resultados de la última corrida

| Comando | Resultado |
|---|---|
| `.\gradlew.bat assembleDebug` | Correcto. Genera `app/build/outputs/apk/debug/app-debug.apk`. |
| `.\gradlew.bat testDebugUnitTest` | 5 pruebas correctas: 1 de plantilla y 4 de `FormRulesTest`. |
| `.\gradlew.bat connectedDebugAndroidTest -PmpazIsolatedTests=true` | 14 pruebas correctas, 0 fallidas, 0 omitidas. |
| `.\gradlew.bat lintDebug` | 0 errores, 29 advertencias y 4 sugerencias. |

Las 14 pruebas instrumentadas se reparten así:

- `ExampleInstrumentedTest`: 1 prueba de contexto.
- `SchoolFlowTest`: 6 pruebas de interfaz, incluidos perfiles, inicio rediseñado y práctica matemática.
- `SchoolRepositoryTest`: 7 pruebas de base de datos y permisos.

## Qué se comprobó

- Login de estudiante, docente y UTP.
- Inicio Explora con cuatro asignaturas, ocho unidades y avance por unidad.
- Perfil propio de estudiante, docente y UTP, con bloqueo de informes y edición de imagen para el estudiante.
- Apertura de una unidad asignada, práctica y prueba final.
- Progreso de 66% después de dos prácticas y 100% después de la prueba.
- Revisión del último intento y bloqueo de un tercer envío cuando el límite es dos.
- Práctica con respuesta numérica y práctica con orden de pasos.
- Resultado de prueba distinguido de preguntas respondidas, revisión del último intento y comienzo de un intento nuevo.
- Pantalla de imagen del docente y sus botones de cámara y galería habilitados.
- Editor de unidad UTP y cambio de estado.
- Permisos: el estudiante no puede abrir informes, asignar unidades ni cambiar fotos.
- Publicación: no se autoriza ni publica una unidad sin preguntas de práctica y prueba.
- Fechas cerradas: no permiten practicar y no consumen intento si la prueba está incompleta.
- Persistencia al cerrar y reabrir la base.
- Migración desde la versión 1 sin perder prácticas, intentos ni respuestas.
- Observaciones del docente, validación y texto del informe.
- Límite de intentos y guardado conjunto de intento y respuestas.

Las pruebas de interfaz usan datos ficticios. No se usaron personas reales.

## Qué falta comprobar

- Abrir la cámara del emulador o de un teléfono físico, tomar una foto, aceptarla y guardar la copia. En esta corrida solo se comprobó que la pantalla y los botones existen.
- Abrir el selector de galería y elegir una imagen real del dispositivo.
- Escuchar el audio en un teléfono físico. La reproducción usa un WAV local y no se escuchó desde el computador.
- Descargar el informe TXT y abrirlo con otra aplicación. El texto y el permiso del docente sí se comprobaron en las pruebas.
- Probar en un teléfono físico, como pide la guía 13.
- Validar los objetivos y contenidos con el docente.
- Agregar a los integrantes del equipo al tablero Trello y registrar actividad real con comentarios y enlaces a commits.
- Instalar y probar el APK release firmado en un teléfono físico; el archivo ya se genera localmente.

## Notas

- Las advertencias y sugerencias de lint no bloquean la compilación; revisar el informe HTML si se requiere depurarlas.
- La app no tiene backend ni sincronización. Las pruebas no certifican seguridad de producción.
- Las pruebas instrumentadas usan `cl.duoc.m.validation` con `mpazIsolatedTests=true` para no reemplazar datos de la instalación normal. El APK de entrega se construye sin ese parámetro.
- `connectedDebugAndroidTest` necesita el emulador encendido y conectado. Si no aparece en `adb devices`, reiniciarlo con Cold Boot.
