# Relación entre requisitos y solución

| Origen | Aplicación en el MVP | Lugar principal |
|---|---|---|
| Guía 8 | Text, Button, Image, Column, Row, Modifier, Scaffold e identidad Explora | HomeScreen, SchoolComponents, SchoolApp |
| Guía 9 | Window Size Classes, tres tamaños y previews | MainActivity, HomeScreen, HomePreviews |
| Guía 11 | Formulario, errores por campo, StateFlow, ViewModel compartido y rutas NavHost | LoginScreen, StaffScreens, SchoolViewModel, SchoolApp |
| Guía 12 | derivedStateOf, rememberSaveable, color y visibilidad animados, DataStore y carga | LearningScreens, ReadingPreferences |
| Guía 13 | Cámara, galería, FileProvider, vista circular y cancelación | PhotoScreen, MediaFiles, file_paths.xml |
| Parcial 2 | Validaciones desacopladas, iconos de error, MVVM, persistencia, recursos y README | Model, ViewModel, Repository, docs |
| Parcial 3 | Recorrido explicable y ejercicios para modificar código | GUIA_COMPLEMENTARIA, RECORRIDO_DEMOSTRACION |
| Caso: cuatro asignaturas | Ocho unidades y 42 preguntas originales | SeedContent |
| Caso: UTP y docente | Borrador/revisión/autorización/publicación/deshabilitación y asignación | SchoolRepository, StaffScreens |
| Caso: prueba e intentos | Disponibilidad, transacción, historial y revisión | submitTest, TestScreen |
| Caso: informes | Resultados por unidad/objetivo, observación/validación humana y exportación TXT | ReportScreen, reportText |
| Usuario: foto solo docente | Vista previa, copia local, autorización por rol y curso | PhotoScreen, updatePhoto |
| Usuario: cursos y trabajos | Cursos separados, entregas escritas, nota y comentario publicados por docente | WorkScreens, WorkRepository, SchoolWork |
| Usuario: SQLite | SQLiteOpenHelper sin Room | SchoolDatabase |

## Límites explícitos

- Se mantiene Segundo Básico y se utiliza una presentación legible para usuarios de 12 años, según lo acordado.
- Los OA son descriptivos de demostración: el material no incluye la selección oficial validada que menciona el anexo del caso. No se inventan códigos curriculares ni se certifica alineación Mineduc.
- Audio general de instrucciones; no narración de cada explicación ni lector de pantalla propio.
- Prácticas de selección, respuesta numérica y orden de pasos; las pruebas finales se mantienen con selección de alternativas. UTP puede agregar nuevas preguntas de selección. Las otras modalidades vienen en las unidades preparadas. La edición no borra preguntas con respuestas históricas.
- Imágenes de apoyo: identidad Explora, ilustración propia de libro/planta y avatar administrado. No hay gestor multimedia UTP de audios/imágenes arbitrarias.
- Una cuenta UTP y una docente de prueba; tres estudiantes ficticios de dos cursos. Sin altas institucionales ni sincronización remota. Backend REST no implementado por quedar fuera de las guías y del alcance local acordado.
- El informe es descriptivo y el docente interpreta los datos.
- Prueba física, OA validados, nombres/número de equipo y Trello requieren la participación del equipo. APK release firmado requiere una clave que el equipo controle. No se afirmará su cumplimiento sin evidencia.

La evaluación exige dos recursos nativos aunque el caso no requiera cámara. Se eligió su uso opcional administrado por el docente para seguir la guía 13; confirmar con el docente que esta adaptación del caso sea aceptada.
