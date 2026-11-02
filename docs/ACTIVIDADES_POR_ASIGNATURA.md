# Actividades por asignatura

Se conserva Segundo Básico y se mantiene una presentación clara para estudiantes de 7 u 8 años que refuerzan ese nivel. Son ejercicios originales, sin atribuirles códigos OA oficiales; deben validarse pedagógicamente con el docente.

| Asignatura | Unidad nueva | Práctica | Prueba final |
|---|---|---|---|
| Lenguaje | La biblioteca viajera | Ordenar tres hechos de un relato, elegir un título global e inferir por qué se repara un libro usando pistas del texto | Secuencia temporal, información explícita y propósito del relato |
| Matemática | Resolvemos en la feria | Escribir un número formado por decenas/unidades, resolver suma y resta contextualizadas y completar una secuencia de cinco en cinco | Valor posicional, elegir operación y resultado, resolver una resta |
| Ciencias Naturales | Observamos el agua | Clasificar el hielo, ordenar una experiencia de congelación, comparar observaciones y predecir un cambio de estado | Distinguir sólido/líquido, explicar congelación y elegir una observación segura |
| Historia y Geografía | Un plano para nuestra comunidad | Usar este/oeste, seguir recorridos de dos pasos en un plano y elegir acuerdos para compartir espacios | Usar sur/norte, interpretar rutas y reconocer cuidado de espacios comunes |

Las cuatro unidades agregan 14 preguntas de práctica y 12 de prueba. Se suman a las 16 preguntas originales, que se mantienen con su historial.

## Cómo se responde

- Selección: tocar una alternativa y leer la retroalimentación.
- Número: escribir el resultado y pulsar Comprobar resultado. No se acepta texto como número.
- Orden: tocar cada paso de primero a último y pulsar Comprobar orden; Empezar de nuevo permite reconstruirlo.
- Las respuestas numéricas y secuencias incorrectas muestran una explicación y permiten corregir. Solo se marca progreso cuando quedan correctas; los borradores de esas respuestas usan rememberSaveable, no generan intentos de prueba.
- Al completar una práctica numérica/secuencial queda bloqueada como completada. Las respuestas correctas se conservan en SQLite y se muestran al volver a entrar.
- Consultar texto y ejemplo permite volver a leer el material sin salir de la práctica. En la prueba se permite consultar la lectura o el plano, para evaluar comprensión y orientación en lugar de memoria del material.

## Actualizar sin perder datos

La versión 2 de SchoolDatabase ejecuta ALTER TABLE para añadir el tipo de actividad y luego inserta las unidades nuevas con identificadores generados por SQLite. Conserva cuentas, fotografías, observaciones, unidades creadas por UTP, respuestas e intentos anteriores. No es necesario desinstalar ni borrar datos. Las cuentas ficticias Sol y Luna reciben las nuevas asignaciones; el docente puede modificar sus fechas e intentos.

## Ubicar y modificar el código

- SubjectActivities.kt: textos, ejemplos y ejercicios nuevos. Task describe una pregunta; UnitContent reúne una unidad. Son clases de datos y listas, como en las guías.
- ActivityKind y FormRules.activityResponse: distinguen el formato y validan respuestas en la lógica.
- SchoolViewModel.submitActivity: comprueba la respuesta, actualiza el mensaje y guarda el progreso correcto.
- LearningScreens.kt: TextField para números, botones para ordenar y plano construido con Column/Row/Text/Surface. No se usan librerías de arrastre ni técnicas de interacción avanzadas.
- SchoolDatabase.onUpgrade: migración SQLite. Se prueba usando el esquema real de la versión anterior, conservado como fixture solo para pruebas.

No cambiar directamente ejercicios ya respondidos: añadir otra unidad o realizar una migración documentada. Los cambios hechos únicamente en las listas de semillas no actualizan instalaciones existentes.
