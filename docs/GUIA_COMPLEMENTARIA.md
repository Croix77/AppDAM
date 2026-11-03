# Guía complementaria: comprender las incorporaciones del MVP

Este documento distingue lo enseñado en las guías 8, 9, 11, 12 y 13 de lo agregado para resolver el caso. No pretende sustituir su estudio. Los ejemplos se encuentran en archivos pequeños organizados por responsabilidad.

## 1. Recorrido del dato: MVVM de la guía 11

Un botón llama una función del `SchoolViewModel`. El ViewModel valida, solicita al repositorio que consulte/guarde y actualiza `SchoolUiState`. La pantalla observa `state.collectAsState()` y se recompone. No se consulta SQLite desde un `Text` o un `Button`.

Ejemplo: seleccionar una alternativa llama `answerPractice`. El repositorio comprueba disponibilidad y guarda. El ViewModel vuelve a consultar respuestas y avance. `PracticeScreen` muestra la explicación usando `AnimatedVisibility` y cambia el color como en la guía 12.

Ejercicio: cambiar el texto de retroalimentación sin modificar el repositorio. Luego ubicar qué función decide si una respuesta es correcta.

## 2. SQLite: complemento solicitado por el equipo

La guía 12 enseña DataStore para una preferencia. No enseña tablas relacionales. SQLite permite relacionar estudiante, unidad, pregunta, respuesta e intento, y está contemplado en el caso. Se utiliza `SQLiteOpenHelper` directamente para evitar incorporar Room y procesadores de código no desarrollados en las guías.

`SchoolDatabase.onCreate` crea las tablas al abrir una base nueva. `SeedContent.insert` carga cuatro unidades originales y `SubjectActivities.insert` añade otras cuatro con formatos propios de cada asignatura. Volver a abrir una base existente no vuelve a cargar las semillas ni elimina el progreso. La migración de versión 1 a 2 agrega la columna kind con ALTER TABLE e inserta las unidades nuevas sin borrar el historial. Una futura versión necesitará su propia migración explícita.

La implementación y el propósito de las nuevas actividades se explican en ACTIVIDADES_POR_ASIGNATURA.md. Los ejercicios numéricos utilizan TextField y los de secuencia botones con estado guardable; su validación se mantiene en la lógica. No requieren técnicas de arrastrar/soltar ni nuevas dependencias.

Tablas:

| Tabla | Responsabilidad |
|---|---|
| users | Perfil ficticio, rol, docente asignado y ruta de imagen |
| lessons / questions | Unidad, objetivo, contenido, estado y preguntas |
| assignments | Estudiante/unidad, fechas y límite de intentos |
| practice | Última respuesta de práctica por estudiante/pregunta |
| attempts / test_answers | Historial de intentos y respuestas de cada envío |
| notes | Observación y validación docente por estudiante/unidad |
| audit | Autor, fecha y acción de gestión |

Las claves primarias identifican filas; las foráneas relacionan registros existentes. La combinación estudiante/unidad/número impide repetir un número de intento. El progreso se calcula desde las respuestas e intentos guardados, sin mantener un segundo contador que pueda desincronizarse.

Ejemplo en `SchoolRepository`:

```kotlin
db.rawQuery("SELECT * FROM users WHERE username=?", arrayOf(username))
```

El signo `?` se sustituye con un argumento, no con código SQL. Así una comilla en un usuario no altera la consulta. `use` cierra el Cursor al terminar. `ContentValues` permite insertar campos con nombre. `INSERT OR REPLACE` se usa solo en tablas sin hijos para actualizar una respuesta/asignación/observación.

## 3. Trabajo en segundo plano y transacciones

Leer archivos, derivar claves o consultar disco puede bloquear el dibujo. `viewModelScope.launch` inicia la operación y `withContext(Dispatchers.IO)` la ejecuta en un hilo apropiado para disco. El indicador de carga corresponde a una operación real. Son herramientas complementarias para conectar el estado de la guía 11 con SQLite, no una capa arquitectónica nueva.

`submitTest` abre una transacción, comprueba el límite, inserta el intento y sus respuestas, y llama `setTransactionSuccessful`. `finally` termina la transacción incluso ante un error. Si falta una respuesta o falla una inserción, no queda un intento a medias. El botón se deshabilita durante el guardado y se borran las elecciones al finalizar, evitando reenvíos involuntarios de un mismo formulario.

Ejercicio: localizar el `require` que controla el máximo de intentos y explicar por qué se comprueba dentro de la transacción.

## 4. Login local y autorización por rol

El formulario y su validación siguen la guía 11. Autenticar es verificar una cuenta; autorizar es comprobar si esa cuenta puede ejecutar una acción. Ocultar el botón de cámara al estudiante no basta: `updatePhoto` exige un docente y comprueba que el estudiante pertenece a su curso. `report` hace la misma comprobación. UTP administra contenidos, pero no abre informes docentes.

No guardamos claves en texto plano dentro de la base. `Passwords` crea una sal aleatoria y deriva la clave con PBKDF2; la comparación usa `MessageDigest.isEqual`. La sal no es una contraseña y puede guardarse junto a la derivación. Estas técnicas no están explicadas en las guías: se incluyen para no enseñar almacenamiento de contraseñas en claro.

Las credenciales de demostración siguen visibles en `SeedContent`/`SchoolDatabase` y README porque el prototipo usa cuentas ficticias públicas. Esta aplicación local no ofrece seguridad equivalente a un servidor. No usarla con cuentas reales. La sesión se pierde cuando el proceso termina; al rotar, el ViewModel mantiene su estado. La contraseña escrita no se conserva con `rememberSaveable`.

Ejercicio: intentar iniciar sesión con un usuario vacío y después con una clave incorrecta. Identificar `FormRules.login`, `loginErrors` y el mensaje de campo.

## 5. Reglas pedagógicas del caso

La secuencia de estados se implementa con un `enum` y un `when`: borrador, en revisión, autorizado, publicado y deshabilitado. Antes de autorizar/publicar se exige al menos una pregunta de práctica y una de prueba. El docente solo asigna material autorizado/publicado; el estudiante solo lista material publicado y asignado. Se conservan las preguntas históricas en lugar de eliminarlas junto con respuestas previas.

Las fechas usan `LocalDate` y formato AAAA-MM-DD. Las marcas de tiempo de guardado usan AAAA-MM-DD HH:MM:SS. Las fechas ISO válidas se ordenan cronológicamente al compararlas como texto. Las fechas dependen del reloj del dispositivo; un prototipo local no puede protegerlas de cambios del reloj.

El avance cuenta prácticas correctas y prueba rendida; 100% significa recorrido completado, no nota perfecta. La prueba muestra el número de respuestas correctas. El informe conserva los intentos y las respuestas del último intento; las observaciones son redactadas por el docente. No hay diagnósticos automáticos.

## 6. Cámara, galería y copia privada

La selección/captura sigue la guía 13: `GetContent`, `TakePicture`, URI y FileProvider. Solo el docente ve esos lanzadores. Se muestra primero una vista previa y después se confirma Guardar. Cancelar no cambia la imagen anterior.

No se solicita un permiso amplio de almacenamiento ni acceso directo a la cámara: el sistema abre otra aplicación mediante los contratos y concede acceso al recurso seleccionado. FileProvider comparte temporalmente solo `cache/camera/`, con `exported=false`; no expone toda la carpeta privada. Por eso el manifiesto no declara CAMERA ni READ_EXTERNAL_STORAGE innecesarios para este flujo delegado.

La parte adicional está en `MediaFiles`: una URI de selección puede perder su acceso, por eso se copia, comprueba que sea imagen, reduce su tamaño y guarda JPEG en `files/avatars`. La base guarda la ruta, no la imagen como texto ni la URI temporal. La selección tiene un límite de 16 MB. La captura se decodifica con muestreo para limitar memoria. El avatar predeterminado no requiere foto.

La orientación EXIF de ciertas cámaras puede requerir tratamiento adicional en otros teléfonos; verificar con el dispositivo físico objetivo. Las vistas previas temporales pueden permanecer en caché hasta que Android la libere. Las fotos guardadas se eliminan al restaurar avatar o reemplazarlas.

Ejercicio: elegir una imagen, cancelar la vista previa, cerrar y abrir la app y comprobar que la imagen anterior permanece. Después guardar una y verificarla entrando como estudiante.

## 7. Audio empaquetado

Las guías no desarrollan reproducción de audio y el caso requiere instrucciones sonoras. `AudioHelp` usa `MediaPlayer` con `res/raw/instrucciones.wav`. No necesita internet, micrófono ni un motor de voz instalado. Al salir de la pantalla se libera el reproductor con `DisposableEffect`; al terminar o fallar también se libera.

El archivo es una narración sintética local de un texto de instrucciones original. Actualmente acompaña el recorrido general; no lee automáticamente todo el contenido ni incorpora reconocimiento de voz. Para cambiarlo, grabar o generar un audio autorizado con el mismo nombre, sin modificar las pantallas.

## 8. Exportación de informe

`CreateDocument("text/plain")` abre el selector del sistema. El docente elige ubicación y nombre; el resultado es una URI. Con `openOutputStream` escribimos el informe en UTF-8. Cancelar no escribe nada. No requiere permisos de acceso a todos los archivos. Es una incorporación adicional para satisfacer la descarga de informes del caso; no implementa PDF ni servicios externos.

Ejercicio: descargar el informe, abrirlo y comparar sus resultados con la pantalla. No distribuirlo con datos reales de estudiantes.

## 9. Adaptabilidad, preferencias y estado

`calculateWindowSizeClass` aplica la guía 9: compacto usa una columna, medio dos y expandido tres. `Page` limita ancho y permite desplazar el contenido; `HomePreviews` ofrece vistas para los tres tamaños. No se modifica el SDK para simular un tamaño.

`ReadingPreferences` aplica DataStore de la guía 12 a una preferencia booleana. A+/A− cambia la tipografía y conserva la selección al reiniciar. No se guardan respuestas en DataStore: esas relaciones están en SQLite. `derivedStateOf` calcula si la práctica ya habilita la prueba y `AnimatedVisibility` anima la explicación después de responder.

## 10. Verificación y defensa

Las pruebas añadidas son un complemento, no código de interfaz que debas memorizar. `FormRulesTest` comprueba entradas inválidas. `SchoolRepositoryTest` usa una base separada que crea y elimina en cada prueba; comprueba permisos, fechas, persistencia e intentos. `SchoolFlowTest` recorre la interfaz real con Compose Test y guarda evidencias ficticias en Pictures/MPAZTest del emulador. No confundirlo con el funcionamiento productivo de galería.

Para practicar: agregar una validación, cambiar un componente, identificar una consulta y mostrar persistencia al reiniciar. El recorrido completo está en RECORRIDO_DEMOSTRACION.md. Antes de la defensa, estudiar las funciones y practicar el recorrido.

## Login y textos de pantalla

El login reutiliza el logo y la ilustración existentes. La composición usa Column, Row, Image, Surface, campos y Button de Material 3. BoxWithConstraints permite consultar el ancho disponible: desde 840 dp, ilustración y formulario se ubican en dos columnas. En teléfono aparecen uno debajo del otro, con desplazamiento para pantallas pequeñas y teclado abierto. No se agregan dependencias.

Los accesos de prueba se muestran solo al tocar su botón; un Boolean con rememberSaveable controla ese bloque. Mostrar u ocultar la clave alterna PasswordVisualTransformation y VisualTransformation.None, sin cambiar la autenticación. Como complemento de teclado, ImeAction.Next permite avanzar y KeyboardActions con ImeAction.Done ejecuta el mismo envío del botón Entrar. La clave sigue sin guardarse con rememberSaveable y se limpia al enviar.

Se retiraron de las pantallas los avisos repetidos sobre el prototipo y los comentarios técnicos de implementación. Las restricciones, autoría, alcance académico y uso de cuentas ficticias permanecen documentados. Se mantienen las explicaciones pedagógicas, los errores, el progreso y la confirmación de guardado.


## Trabajos por curso y corrección manual

La ampliación usa tres tablas SQLite nuevas y migración de versión 2 a 3, sin eliminar las anteriores. La asignación al curso guarda sus destinatarios; la respuesta puede permanecer como borrador o entregarse. El repositorio distingue el borrador privado de respuesta, la entrega y la corrección provisional del docente. Solo publicar hace visibles la nota y el comentario al estudiante.

La nota se guarda en décimas enteras y se presenta con coma (65 → 6,5). La validación rechaza más de un decimal y valores fuera de rango. Entrega y publicación usan transacciones y comprueban rol, destinatario, plazo y estado. El detalle paso a paso, las clases y los límites están en TRABAJOS_Y_CURSOS.md.

Las pantallas reutilizan los componentes de las guías. FilterChip elige asignatura/pestaña; AlertDialog confirma las acciones finales; imePadding deja espacio al teclado dentro de las páginas desplazables. No se agrega una librería ni un servidor.
