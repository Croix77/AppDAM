# Planificación del equipo 5

Proyecto: MPAZ PRO 2.º · DSY1105 · Duoc UC.

Integrantes:

- NQDESTROYER
- Croix77

Tablero creado: [MPAZ PRO 2.º - Equipo 5](https://trello.com/invite/b/6ae90a63404d0afa0cd31554/ATTI360830b7568f314d8657f806489c8ab756B22D6F/mpaz-pro-2-equipo-5-%F0%9F%93%B1). El enlace es de invitación.

## Cómo mantener el Trello

1. Verificar que el tablero existente conserve el nombre `MPAZ PRO 2.º - Equipo 5`.
2. Crear cuatro listas: `Por hacer`, `En progreso`, `En revisión` y `Hecho`.
3. Importar `docs/trello_import.csv`; sus tarjetas incluyen hashes y autoría Git verificable.
4. Agregar a los dos integrantes como miembros del tablero.
5. Mantener el enlace del tablero en el README y en la entrega; usa el enlace de invitación solo con las personas que deban unirse.
6. Mover las tarjetas a medida que avanza el trabajo. En cada una dejar un comentario con lo que se hizo y el enlace al commit.
7. No cambiar fechas ni participación para que el tablero se vea más completo. Es mejor que el historial sea real aunque empiece hoy.

Los responsables de la tabla son una propuesta para repartir la revisión y la defensa. Si el trabajo real no coincide, cambiar el nombre antes de usar el tablero como evidencia. Las tarjetas en `Hecho` son funciones que ya están en el repositorio; cada integrante agrega su comentario solo si participó en esa tarea.

## Tarjetas

| # | Tarjeta | Lista | Responsable sugerido | Qué debe quedar |
|---|---|---|---|---|
| 1 | Login y validaciones por campo | Hecho | Croix77 | Tres roles pueden entrar; los errores se ven bajo cada campo; la clave se puede mostrar u ocultar. |
| 2 | Navegación por rol | Hecho | NQDESTROYER | Estudiante, docente y UTP llegan solo a sus pantallas; la barra superior permite volver y cerrar sesión. |
| 3 | Base SQLite y migración v1 a v2 | Hecho | Croix77 | Se guardan usuarios, unidades, preguntas, intentos y respuestas; al actualizar no se pierden datos. |
| 4 | Cuatro asignaturas y ocho unidades | Hecho | NQDESTROYER | Lenguaje, Matemática, Ciencias y Historia tienen dos unidades y preguntas originales. |
| 5 | Práctica y prueba final | Hecho | Croix77 | La práctica da retroalimentación; la prueba se habilita al completar la práctica y descuenta intentos. |
| 6 | Asignaciones, fechas e intentos | Hecho | NQDESTROYER | El docente asigna unidades autorizadas o publicadas y controla fechas y máximo de intentos. |
| 7 | Informe y observaciones | Hecho | Croix77 | El docente ve resultados por unidad, guarda una observación, la valida y descarga un TXT. |
| 8 | Gestión UTP | Hecho | NQDESTROYER | UTP crea borradores, agrega preguntas, revisa, autoriza, publica, deshabilita y ve el historial. |
| 9 | Cámara, galería e imagen del estudiante | Hecho | Croix77 | El docente elige o toma una imagen ficticia, la confirma y el estudiante solo la ve. |
| 10 | Audio y descarga del informe | Hecho | NQDESTROYER | El audio local se reproduce y se detiene; el informe se guarda con el selector de documentos. |
| 11 | Accesibilidad y tamaños de pantalla | Hecho | Croix77 | A+/A− cambia el texto y se conserva; la interfaz se adapta a ancho compacto, medio y expandido. |
| 12 | Pruebas unitarias e instrumentadas | Hecho | NQDESTROYER | 5 pruebas unitarias y 14 instrumentadas pasan en el emulador; el resultado queda documentado. |
| 13 | README, arquitectura y Trello | Hecho | NQDESTROYER | README con enlace; arquitectura actualizada por NQDESTROYER; tablero creado. |
| 14 | Revisión de íconos y animaciones | Hecho | NQDESTROYER | Íconos Material en login y errores; transición suave entre pantallas; verificado al compilar. |
| 15 | Prueba en teléfono físico | Por hacer | Croix77 | Cámara, galería, audio y descarga probados en un teléfono real; registrar modelo y resultado. |
| 16 | Validar objetivos con el docente | Por hacer | NQDESTROYER | El docente revisa los objetivos descriptivos y confirma que sirven para la demostración. |
| 17 | APK release firmado | Hecho | NQDESTROYER | APK release firmado con clave local fuera del repositorio; conservar copia segura de la clave del equipo. |
| 18 | Informe consolidado por curso | Por hacer | NQDESTROYER | Opcional. Agregar un resumen por curso u objetivo si alcanza el tiempo; no reemplaza el informe por estudiante. |
| 19 | Audios e imágenes por unidad para UTP | Por hacer | Croix77 | Opcional. Evaluar si se agrega un administrador simple; hoy solo existe el audio general y la imagen del estudiante. |

## Orden sugerido

1. Terminar las tarjetas 13, 14 y 15.
2. Validar la 16 antes de la presentación.
3. Dejar la 17 para el final y solo si el docente la pide.
4. Las tarjetas 18 y 19 son mejoras; no deben retrasar la entrega base.

## Evidencias que conviene guardar

- Captura del tablero con las listas y tarjetas.
- Comentarios con enlaces a commits de cada integrante.
- Capturas de la app en el emulador y en el teléfono físico.
- Resultado de `assembleDebug`, `testDebugUnitTest`, `connectedDebugAndroidTest` y `lintDebug`.
- Enlace al APK debug o al release si corresponde.
