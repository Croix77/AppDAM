# MPAZ PRO 2.º

Aplicación Android hecha para DSY1105 de Duoc UC. Sirve para reforzar contenidos de Segundo Básico con textos cortos, audio y ejercicios. Es un prototipo local para la entrega académica, no un sistema escolar real.

**Equipo 5**
- NQDESTROYER
- Croix77

## Qué se puede hacer

- Entrar como estudiante, docente o UTP.
- Ver cuatro asignaturas y ocho unidades con explicación, ejemplo y audio.
- Practicar con alternativas, respuesta numérica u orden de pasos.
- Rendir una prueba final cuando la práctica está completa.
- Revisar intentos, avance y respuestas anteriores.
- Asignar unidades, fechas e intentos desde el perfil docente.
- Revisar informes, guardar observaciones y descargar un TXT.
- Crear, revisar, publicar y deshabilitar unidades desde UTP.
- Cambiar la imagen ficticia del estudiante desde el perfil docente.
- Aumentar o disminuir el tamaño del texto con A+/A−.

El contenido es de demostración. Los objetivos están escritos de forma simple y deben ser revisados por el docente; no son códigos OA oficiales.

## Ejecutar

1. Abrir la carpeta raíz en Android Studio y esperar la sincronización de Gradle.
2. Usar Android SDK 36, Gradle 8.13, AGP 8.13.2 y Kotlin 2.0.21.
3. Crear o iniciar un emulador con API 33 o superior. La app instala desde API 26. Las pruebas de este equipo se corrieron en API 37.
4. Ejecutar el módulo `app`. La app funciona sin servidor ni internet; la primera sincronización sí descarga dependencias.

En este computador se compila con JDK 17 (Temurin). Android Studio trae JBR 25 y esta versión de AGP no lo acepta: si aparece un error con `25.0.4`, ir a **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK** y elegir el JDK 17.

En PowerShell:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat lintDebug
```

El APK de prueba queda en `app/build/outputs/apk/debug/app-debug.apk`. No es un APK release firmado.

## Cuentas de prueba

| Rol | Usuario | Clave |
|---|---|---|
| Estudiante | estudiante | Aprendo123 |
| Estudiante | luna | Aprendo123 |
| Docente | docente | Profe123 |
| UTP | utp | Utp12345 |

Las cuentas se crean solas al abrir la app por primera vez. Son ficticias; no usar nombres, fotos, RUT ni claves de personas reales.

## Estructura

```
app/src/main/java/cl/duoc/m/
  model/         datos, roles y reglas de validación
  repository/    SQLite, consultas, datos iniciales, archivos y DataStore
  viewmodel/     estado de las pantallas y acciones
  navigation/    rutas y menú por rol
  ui/            pantallas, componentes y tema
```

La explicación completa está en [docs/ARQUITECTURA.md](docs/ARQUITECTURA.md). También se mantiene [arquitectura.txt](arquitectura.txt) en la raíz.

Se usó lo visto en las guías: componentes, adaptabilidad, formularios, ViewModel, StateFlow, DataStore, animaciones, cámara y galería. No se agregó backend, Retrofit, Hilt, Room ni KSP.

## Base de datos

La app usa SQLite con `SQLiteOpenHelper`, sin Room. Guarda usuarios, unidades, preguntas, asignaciones, prácticas, intentos, respuestas, observaciones e historial UTP. La versión 2 conserva los datos de la versión 1 y las preguntas con respuestas históricas no se borran.

## Pruebas

Resultados de la última corrida:

- `assembleDebug`: correcto.
- `testDebugUnitTest`: 5 pruebas correctas.
- `connectedDebugAndroidTest`: 12 pruebas correctas en el emulador `Medium_Phone_API_37.0` (API 37).
- `lintDebug`: 0 errores, 24 advertencias y 1 sugerencia. No hay errores que impidan compilar.

El detalle está en [docs/VERIFICACION.md](docs/VERIFICACION.md). Antes de entregar falta probar cámara y galería en un teléfono físico, escuchar el audio en el dispositivo y validar los objetivos con el docente.

## Trello y evidencias

El plan está en [docs/PLANIFICACION.md](docs/PLANIFICACION.md). Las tarjetas con autoría y hashes Git están en [docs/trello_import.csv](docs/trello_import.csv). El tablero ya está creado: [MPAZ PRO 2.º - Equipo 5](https://trello.com/invite/b/6ae90a63404d0afa0cd31554/ATTI360830b7568f314d8657f806489c8ab756B22D6F/mpaz-pro-2-equipo-5-%F0%9F%93%B1). Este enlace invita a quien lo tenga a unirse al tablero.

Las tarjetas se mueven a medida que avanza el trabajo. Cada integrante deja sus comentarios y el enlace al commit cuando corresponde. No se inventan fechas ni participaciones.

## Alcance

- Un solo dispositivo: no sincroniza entre teléfonos.
- Sin backend, sin registro público y sin altas de instituciones o cursos.
- Informes solo para el docente; el estudiante ve su propio avance.
- La imagen la administra el docente y debe ser ficticia.
- No hay diagnósticos automáticos ni inteligencia artificial.
- El APK release firmado queda pendiente si el docente lo exige.
