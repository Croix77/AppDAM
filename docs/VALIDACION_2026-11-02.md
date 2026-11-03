# Validación ampliada de Explora Marcela Paz

Fecha: 2 de noviembre de 2026. Código base: c337cff, rama feature/cuaderno-exploracion. Se ampliaron las pruebas; no se modificó el código funcional de la aplicación.

## Alcance y protección de datos

Se utilizó Pixel 3a, API 37, con la variante `-PmpazIsolatedTests=true` (paquete `cl.duoc.m.validation`). Las pruebas de repositorio usan bases independientes que se eliminan al terminar. No se borró ni reinstaló el paquete habitual `cl.duoc.m`.

## Comprobaciones realizadas

Resultado de emulador: **24/24 pruebas aprobadas en tamaño compacto** (1080 × 2220, densidad 440) y **11/11 recorridos de interfaz aprobados en tamaño expandido simulado** (1600 × 1200, densidad 160). Se restauró la resolución y densidad original al finalizar. Los resultados XML se conservaron por separado para que una ejecución no reemplace la evidencia de la otra.

Comprobación final: `assembleDebug`, `assembleRelease`, `cleanTestDebugUnitTest testDebugUnitTest` y `lintDebug` correctos. Las cinco pruebas unitarias se volvieron a ejecutar (una es la prueba de plantilla). El APK release generado está **sin firmar**: compilar esa variante no completa la entrega firmada del equipo. El último build debug se generó sin el parámetro de pruebas aisladas, por lo que vuelve a utilizar el identificador habitual. Lint terminó sin errores bloqueantes, con 28 advertencias y cuatro sugerencias.

- Inicio de sesión vacío, clave incorrecta y recuperación con una cuenta válida; perfiles de Sol, Luna, docente y UTP; cierre de sesión y ajustes de lectura.
- Apertura de las ocho unidades en las cuatro asignaturas, material escrito, bloqueo de prueba antes de completar práctica y conservación de la unidad al recrear la actividad.
- Práctica de selección, respuesta numérica incorrecta/correcta y orden de pasos; navegación anterior/siguiente, contadores y finalización.
- Envío de prueba, diferencia entre respuestas completadas/correctas, revisión del último intento y preparación de un nuevo intento.
- Completar las ocho unidades desde SQLite, sin transferir avances entre estudiantes; bloqueo antes de terminar práctica, límites de intentos y reapertura de la base.
- Fechas pasadas/futuras e inválidas, límite de intentos fuera de rango, reducción por debajo de intentos usados, respuestas incompletas/fuera de rango y preguntas ajenas al examen.
- Permisos de estudiante, docente y UTP; rechazo de operaciones sobre un estudiante sin docente asignado; bloqueo de informes, fotos, asignaciones e historial para roles no autorizados.
- Observación docente vacía, guardado, validación y consulta posterior. Generación del texto del informe desde el repositorio.
- UTP: validación de campos vacíos, creación de borrador, preguntas de práctica y prueba, revisión, autorización, publicación, deshabilitación e historial.
- Migración SQLite v1 a v2 conservando respuestas e intentos anteriores.
- Abrir cámara/galería del sistema y cancelar sin guardar una imagen nueva.
- Importación de imagen, reducción a un máximo de 1024 píxeles por dimensión, copia JPEG privada y rechazo de archivos inválidos/rutas fuera de caché.
- Escritura y lectura de captura mediante FileProvider; preparación, inicio y detención del audio empaquetado con MediaPlayer.

## Incidencias durante la validación

La primera ejecución falló en seis pruebas de interfaz porque el emulador no mostraba una ventana activa. Tras despertar y desbloquear el dispositivo, la misma batería base y las pruebas ampliadas de repositorio pasaron.

Dos recorridos nuevos requirieron corregir su sincronización y los textos esperados. Los mensajes temporales de guardado podían cubrir el botón siguiente cuando la automatización intentaba pulsarlo inmediatamente. Se verificó el guardado y se ajustaron las pruebas para esperar a que desaparezcan las confirmaciones. No se modificó la aplicación para ocultar un fallo ni se contaron las ejecuciones fallidas como aprobadas. La superposición temporal es un detalle de usabilidad que puede mejorarse.

## Límites

Esta revisión cubre recorridos principales y casos de error concretos; no demuestra que todas las combinaciones posibles estén libres de defectos. La recreación de actividad no equivale a matar el proceso por falta de memoria.

No se volvieron a verificar en esta sesión la aceptación de una captura real desde la cámara, la selección efectiva de una foto desde el selector ni el guardado del informe mediante el diálogo del sistema y su apertura en otra aplicación. La captura/selección se habían comprobado en la entrega base; las pruebas nuevas verifican cancelación y procesamiento de archivos. Tampoco sustituyen pruebas en teléfono físico, audibilidad humana, orientación EXIF, TalkBack, validación pedagógica ni firma release.

Las advertencias de lint sobre recursos sin uso y mantenimiento de herramientas permanecen visibles; no se actualizan dependencias como parte de esta validación.
