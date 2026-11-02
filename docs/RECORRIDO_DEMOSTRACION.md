# Recorrido de comprobación y estudio

## Estudiante

1. Entrar con `estudiante / Aprendo123`.
2. Abrir Lenguaje y leer el objetivo, texto y ejemplo. Probar el audio y detenerlo.
3. Pulsar Practicar. Responder una alternativa incorrecta: aparece explicación. Seleccionar la correcta y completar la segunda pregunta.
4. Volver a la unidad. El avance es 66% (dos prácticas correctas de tres pasos, incluyendo la prueba).
5. Rendir la prueba y enviar ambas respuestas. Se registra un intento, se muestran resultados y se puede revisar cada respuesta.
6. Volver y comprobar 100% de recorrido. Repetir la prueba hasta agotar el límite; ya no se permite otro envío.
7. Salir, volver a entrar o cerrar la aplicación y comprobar que el progreso no desaparece.
8. Confirmar que no hay botones para cambiar foto ni abrir informes pedagógicos.

## Docente

1. Entrar con `docente / Profe123`.
2. Seleccionar Estudiante Sol o Luna. Cambiar una asignación con fechas válidas y entre uno y cinco intentos. Probar fechas inválidas para ver errores por campo.
3. Abrir un informe. Revisar objetivo, intentos y respuestas. Redactar una observación, validarla y guardar. Descargar TXT y abrirlo.
4. Administrar imagen. Abrir galería, elegir una imagen de prueba, revisar y Guardar. Repetir con cámara. Cancelar alguna captura y verificar que no se modifica la imagen anterior.
5. Cerrar y abrir la app. Entrar como estudiante y comprobar que ve la imagen y no puede editarla.

## UTP

1. Entrar con `utp / Utp12345`.
2. Crear una unidad con todos los campos y autoría explícita. Guardar borrador.
3. Agregar al menos una pregunta de práctica y otra de prueba, con tres alternativas distintas y explicación.
4. Pasar a en revisión, autorizado y publicado.
5. Entrar como docente y asignar la unidad a un estudiante.
6. Entrar como estudiante y realizarla. Deshabilitarla como UTP y verificar que deja de aparecer al volver a entrar.
7. Revisar el historial UTP.

## Tamaños y accesibilidad

Probar compacto, medio y expandido. Rotar el dispositivo en medio de un formulario y de una prueba sin enviar. Cambiar A+/A− y volver a iniciar: la preferencia se conserva. Verificar que se pueda desplazar hasta todos los controles y que no queden ocultos tras el teclado. Probar cámara/galería en teléfono físico: el emulador no reemplaza esa evidencia de la guía 13.

## Para explicar el código

- ¿Quién dibuja? `ui/screens` y `ui/components`.
- ¿Quién gestiona el estado? `SchoolViewModel`, mediante StateFlow.
- ¿Quién comprueba permisos y guarda? `SchoolRepository` y SQLite.
- ¿Quién crea tablas y datos iniciales? `SchoolDatabase` y `SeedContent`.
- ¿Dónde se decide la navegación? `SchoolApp`.
- ¿Por qué el estudiante no cambia fotos? No tiene acceso a la pantalla y el repositorio comprueba el rol/curso.
- ¿Qué pasa al fallar un envío? La transacción no deja un intento parcial y se muestra el error.

Los resultados reales de pruebas se registran en VERIFICACION.md al terminar. Completar integrantes, Trello y evidencias del dispositivo físico antes de entregar; no inventar participación ni fechas.
