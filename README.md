# Explora Marcela Paz

Prototipo académico Android para DSY1105, Duoc UC, basado en el caso MPAZ PRO 2.º de la Escuela Escritora Marcela Paz. La identidad visual implementada se llama Explora Marcela Paz. Apoya el refuerzo de contenidos de Segundo Básico con una interfaz legible para usuarios de 12 años. No es un sistema escolar de producción.

## Ejecutar

1. Abrir la carpeta raíz en Android Studio y esperar la sincronización de Gradle.
2. Usar el JDK incluido con Android Studio (probado con JBR 21), Android SDK 36 y el wrapper Gradle 8.13. AGP 8.13.2 y Kotlin 2.0.21 se conservan de la base que compilaba.
3. Crear un emulador Pixel 3a u otro Android compatible, arrancarlo y ejecutar `app`. Si no conecta, detenerlo y usar Cold Boot. Las guías no exigen un Pixel específico.
4. El mínimo de instalación se conserva en API 26. Para las prácticas de las guías 11-13 usar API 33 o superior. Verificar además en teléfono físico para la guía 13.
5. La aplicación funciona localmente sin servidor ni conexión a internet. La primera sincronización sí descarga dependencias.

En PowerShell, si Java no está en PATH:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest -PmpazIsolatedTests=true
.\gradlew.bat lintDebug
```

El APK debug se genera en `app/build/outputs/apk/debug/app-debug.apk`. No confundirlo con un APK release firmado para entrega: ese requiere la clave privada del equipo, que nunca debe subirse al repositorio.

Las pruebas con `mpazIsolatedTests=true` utilizan el paquete `cl.duoc.m.validation`, separado de la instalación habitual, para proteger el progreso guardado por el usuario. Después de probar, ejecutar `assembleDebug` sin ese parámetro antes de distribuir el APK normal.

## Cuentas ficticias

| Rol | Usuario | Clave |
|---|---|---|
| Estudiante | estudiante | Aprendo123 |
| Estudiante | luna | Aprendo123 |
| Estudiante (2.º Básico B) | mateo | Aprendo123 |
| Docente | docente | Profe123 |
| UTP | utp | Utp12345 |

Estas credenciales son públicas únicamente para la demostración. No usar nombres, fotos, RUT ni claves reales de alumnos. Las cuentas se crean en SQLite al instalar/iniciar la aplicación por primera vez; no hay registro público para autoasignarse roles privilegiados.

## Funcionalidades

- Cursos separados en el panel docente: 2.º Básico A (Sol y Luna) y 2.º Básico B (Mateo).
- Trabajos por curso con instrucciones y fecha límite, borrador y entrega escrita del estudiante. El docente guarda una corrección provisional y publica una nota de 1,0 a 7,0 con comentario. El estudiante solo ve la evaluación publicada.

- Login con validaciones por campo, contraseña oculta y navegación por rol.
- Mi perfil para estudiante, docente y UTP: datos ficticios de la cuenta, curso o área de trabajo, imagen y ajustes de lectura/sesión. El estudiante consulta además su docente y asignaturas asignadas; no puede editar su foto ni abrir informes pedagógicos desde este perfil.
- Inicio con la identidad Explora Marcela Paz, logo de libro/camino/estrella, tarjeta ilustrada y cuatro accesos por asignatura. Contiene ocho unidades y 42 preguntas. Las unidades se muestran al abrir su asignatura; la tarjeta principal permite continuar una unidad real.
- Nuevas prácticas: ordenar hechos de un relato, escribir resultados numéricos, secuenciar una experiencia científica e interpretar un plano de comunidad. Las pruebas finales usan selección de alternativas.
- Explicaciones y ejemplos originales, ilustración vectorial propia e instrucciones de audio empaquetadas. Los objetivos son descripciones de demostración, no códigos OA oficiales certificados.
- Práctica con retroalimentación, progreso SQLite y prueba final habilitada al completar correctamente la práctica.
- Actividades y pruebas muestran su posición (por ejemplo, 1 de 3) y el número completado/respondido. Al terminar se abre una confirmación con el avance guardado. El resultado del examen distingue preguntas respondidas de respuestas correctas y puede consultarse de nuevo sin gastar intentos.
- Botón superior Volver con texto y acciones Volver a la unidad al finalizar.
- Disponibilidad e intentos configurables por docente. Cada envío completo consume un intento; salir sin enviar no lo consume. Transacción SQLite para guardar intento y respuestas conjuntamente.
- Estudiante ve sus propios resultados, sin informes pedagógicos ni edición de imágenes.
- Docente administra sus estudiantes, asigna unidades autorizadas, consulta informes por objetivo/unidad, guarda y valida observaciones y descarga un informe TXT.
- Docente toma o selecciona una imagen ficticia, la revisa y confirma antes de asociarla a un estudiante. Se conserva una copia privada local. El estudiante solo la visualiza. Restauración de avatar predeterminado.
- UTP crea/edita borradores, agrega preguntas, revisa, autoriza, publica y deshabilita unidades; historial de acciones. Las preguntas guardadas se conservan para no alterar respuestas históricas.
- Diseño compacto/medio/expandido con Window Size Classes: una/dos/tres columnas en inicio y contenido con ancho máximo. Formularios desplazables. A+/A− cambia el tamaño de texto y lo conserva con DataStore.
- Animación de visibilidad/color para respuestas y estados de carga reales.

## Estructura y material de referencia

```
app/src/main/java/cl/duoc/m/
  model/         datos y reglas de validación
  repository/    SQLite, consultas, semillas, archivos y DataStore
  viewmodel/     estado de pantalla y acciones con StateFlow
  navigation/    rutas y entradas por rol
  ui/            inicio, componentes, pantallas y tema Material 3
```

Guía 8: componentes base. Guía 9: adaptabilidad. Guía 11: formularios, navegación y ViewModel compartido. Guía 12: estado, DataStore y animaciones. Guía 13: cámara y galería. No se incorporan backend, Retrofit, Hilt, Room ni KSP. Las incorporaciones indispensables no desarrolladas en las guías están explicadas en [la guía complementaria](docs/GUIA_COMPLEMENTARIA.md).

Consultar [Trabajos y cursos](docs/TRABAJOS_Y_CURSOS.md) para el recorrido nuevo, los límites de entrega y la migración SQLite v3.

Leer también [el recorrido de demostración](docs/RECORRIDO_DEMOSTRACION.md) y [la matriz de requisitos](docs/REQUISITOS.md).

## Datos, materiales y alcance

Todos los registros semilla son ficticios. Relato, ejercicios e ilustración `learning_garden.xml` son originales para este prototipo. El logo y la ilustración de inicio corresponden al boceto aprobado; los iconos de asignatura son recursos vectoriales originales. El recurso de meme anterior fue retirado. Los recursos y su preparación están descritos en [Identidad Explora](docs/IDENTIDAD_EXPLORA.md). El audio reproduce instrucciones originales, sin servicios externos ni grabaciones personales.

El almacenamiento es de un solo dispositivo: no sincroniza entre el teléfono del docente y el del estudiante. Los roles permiten demostrar los flujos académicos dentro de una misma instalación, no sustituyen autenticación de servidor. Los informes se interpretan y validan por el docente.

UTP y docente tienen cuentas preconfiguradas; el alta de instituciones, cursos y usuarios no forma parte del MVP. El backend sugerido en el caso queda como evolución posterior. La configuración actual de Kotlin/AGP se conserva porque compila; una actualización de herramientas se debe abordar por separado.

## Equipo y evidencias

Completar con los nombres y número de equipo confirmados antes de entregar. Registrar en Trello las tareas y contribuciones reales. Los commits se realizan por avances comprobados, con fechas y autoría reales. No incluyen los documentos del curso, datos personales ni claves de firma.

Antes de entregar: validar OA y contenidos con el docente, ejecutar cámara/galería en teléfono físico y confirmar la fecha de cierre del repositorio indicada en la pauta.
