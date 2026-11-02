# Arquitectura del MVP MPAZ PRO 2.º

Documento alineado con el código de la rama `tomasdev`. Reemplaza el diseño inicial de seis niveles y doce tablas por el alcance real del MVP: una aplicación Android local, con datos ficticios y tres perfiles.

## 1. Alcance

- Aplicación Android para reforzar contenidos de Segundo Básico (7 u 8 años).
- Un solo dispositivo: no hay sincronización entre teléfonos ni backend.
- Persistencia local con SQLite y una preferencia de accesibilidad con DataStore.
- Contenido de demostración original. Los objetivos son descriptivos y deben validarse con el docente.
- Sin datos reales, sin diagnósticos automáticos y sin inteligencia artificial.

## 2. Roles

| Rol | Puede hacer | No puede hacer |
|---|---|---|
| Estudiante | Ver unidades publicadas y asignadas, leer, escuchar audio, practicar, rendir la prueba y revisar sus respuestas. | Ver informes pedagógicos, cambiar su imagen o administrar contenidos. |
| Docente | Ver sus estudiantes, asignar unidades autorizadas, definir fechas e intentos, revisar informes, guardar observaciones, descargar el informe y administrar la imagen ficticia. | Crear unidades, publicar contenidos o modificar asignaturas. |
| UTP | Crear y editar borradores, agregar preguntas, cambiar estados, publicar o deshabilitar unidades y consultar el historial. | Ver informes pedagógicos de estudiantes. |

## 3. Navegación implementada

| Pantalla | Ruta | Rol | Archivo principal |
|---|---|---|---|
| Inicio de sesión | Sin sesión | Todos | `ui/screens/LoginScreen.kt` |
| Inicio del estudiante | `home` | Estudiante | `ui/HomeScreen.kt` |
| Detalle de unidad y contenido | `lesson` | Estudiante | `ui/screens/LearningScreens.kt` |
| Práctica | `practice` | Estudiante | `ui/screens/LearningScreens.kt` |
| Prueba final y revisión | `test` | Estudiante | `ui/screens/LearningScreens.kt` |
| Inicio del docente o UTP | `home` | Docente / UTP | `ui/screens/StaffScreens.kt` |
| Perfil y asignaciones del estudiante | `student` | Docente | `ui/screens/StaffScreens.kt` |
| Informe pedagógico | `report` | Docente | `ui/screens/StaffScreens.kt` |
| Imagen ficticia del estudiante | `photo` | Docente | `ui/screens/PhotoScreen.kt` |
| Editor de unidad y preguntas | `editor` | UTP | `ui/screens/StaffScreens.kt` |
| Historial de cambios | `audit` | UTP | `navigation/SchoolApp.kt` |

La barra superior permite volver, cambiar el tamaño del texto con A+/A− y cerrar sesión. Las transiciones entre rutas usan un fundido breve.

## 4. Capas y responsabilidades

| Capa | Responsabilidad | Archivos |
|---|---|---|
| `model` | Datos, roles, estados y reglas de validación reutilizables. | `model/SchoolModels.kt` |
| `repository` | Consultas SQLite, permisos, transacciones, archivos y DataStore. | `repository/*.kt` |
| `viewmodel` | Estado de pantalla con `StateFlow`, acciones y errores. | `viewmodel/SchoolViewModel.kt` |
| `navigation` | Rutas, barra superior y entradas según el rol. | `navigation/SchoolApp.kt` |
| `ui` | Pantallas, componentes y tema Material 3. | `ui/**` |

Flujo: una pantalla llama una acción del ViewModel; el ViewModel valida, pide datos al repositorio y actualiza `SchoolUiState`; Compose observa el estado y se recompone. La UI no consulta SQLite directamente.

## 5. Base de datos local (SQLite versión 2)

| Tabla | Responsabilidad |
|---|---|
| `users` | Cuenta ficticia, rol, curso, docente asignado, sal/hash y ruta de imagen. |
| `lessons` | Unidad, asignatura como texto, objetivo, explicación, ejemplo, estado y autoría. |
| `questions` | Preguntas de práctica o prueba, tres alternativas, respuesta correcta, explicación y tipo. |
| `assignments` | Unidad asignada a un estudiante, fechas y máximo de intentos. |
| `practice` | Última respuesta de práctica por estudiante y pregunta. |
| `attempts` | Intentos de prueba con puntaje, total y fecha. |
| `test_answers` | Respuestas seleccionadas en cada intento. |
| `notes` | Observación del docente y su validación. |
| `audit` | Fecha, actor y acción de gestión UTP. |

### Relación con el diseño inicial

| Entidad del diseño inicial | Implementación en el MVP |
|---|---|
| Asignatura y Curso | Campos de texto en `lessons` y `users`. Las cuatro asignaturas son fijas. |
| Unidad | `lessons`. |
| Objetivo de aprendizaje | `lessons.objective` como descripción, sin código OA oficial. |
| Examen | Preguntas con `final_test = 1`, más `attempts` y `test_answers`. |
| Recurso | Audio en `res/raw`, ilustración vectorial, logo y ruta de imagen en `users.photo`. |
| Autonomía y Recurso_Estudiante | No se implementan como tablas: el progreso se obtiene de `practice` y `attempts`. |
| Informe pedagógico | Se construye desde `attempts`, `test_answers` y `notes`; no se duplica en otra tabla. |
| Curso y Estudiante_Curso | `users.course` y `users.teacher_id`; el MVP no administra cursos ni altas. |

La simplificación evita duplicar datos y mantiene el MVP entendible para el nivel del curso. No se usa Room, Hilt, Retrofit ni un backend porque la aplicación es local y las guías no los requieren.

## 6. Reglas de negocio principales

- UTP: `BORRADOR → EN_REVISION → AUTORIZADO → PUBLICADO → DESHABILITADO`.
- Antes de autorizar o publicar se exige al menos una pregunta de práctica y una de prueba.
- El docente solo asigna unidades `AUTORIZADO` o `PUBLICADO`.
- El estudiante solo ve unidades `PUBLICADO` que además le fueron asignadas.
- La prueba final se habilita cuando la práctica está completa; cada envío usa un intento.
- El intento y sus respuestas se guardan en una transacción.
- El informe es exclusivo del docente y no genera diagnósticos automáticos.
- La imagen solo la administra el docente y debe ser ficticia.

## 7. Recursos nativos utilizados

- Cámara y galería mediante contratos de actividad y `FileProvider`, solo para el docente.
- Audio local con `MediaPlayer` desde `res/raw`, sin micrófono ni internet.
- Descarga del informe con el selector de documentos del sistema (`CreateDocument`).

## 8. Fuera de alcance del MVP

- Backend, sincronización, autenticación de servidor y multi-dispositivo.
- CRUD de asignaturas, cursos o instituciones.
- Gestor UTP de audios e imágenes por unidad.
- Informe consolidado por curso u objetivo más allá del detalle por estudiante y unidad.
- Firma release si el docente no la exige; el APK de prueba es debug.
- Diagnósticos automáticos, IA generativa y datos reales de estudiantes.

## 9. Trazabilidad con la rúbrica

| Indicador | Implementación principal |
|---|---|
| IE 2.1.1 Interfaz y navegación | `SchoolApp`, `HomeScreen`, `LearningScreens`, `StaffScreens`, `Page`. |
| IE 2.1.2 Formularios y validación visual | `Field`, `LoginScreen`, `SchoolComponents`, `StaffScreens`. |
| IE 2.2.1 Lógica desacoplada | `FormRules`, `SchoolViewModel`, `SchoolRepository`. |
| IE 2.2.2 Animaciones | `AnimatedVisibility`, `animateColorAsState`, transiciones del `NavHost`. |
| IE 2.3.1 Modularidad y persistencia | Carpetas por capa, `SchoolDatabase`, SQLite, `ReadingPreferences`. |
| IE 2.3.2 GitHub y Trello | Repositorio público, commits por avance y tablero de planificación. |
| IE 2.4.1 Recursos nativos | `PhotoScreen`, `MediaFiles`, `AudioHelp`, descarga de informe. |
