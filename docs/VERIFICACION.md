# Verificación del MVP

## Validación ampliada más reciente

El 2 de noviembre de 2026 se aprobaron 24 pruebas de emulador en tamaño compacto y 11 recorridos de interfaz en tamaño expandido simulado. Se volvieron a ejecutar y aprobaron cinco pruebas unitarias; compilación debug, release sin firmar y lint correctos. Los recorridos, incidencias de las ejecuciones iniciales y límites están detallados en [Validación ampliada](VALIDACION_2026-11-02.md). Las comprobaciones históricas siguientes no sustituyen esos límites.

Fecha: 2 de noviembre de 2026. Entorno local: Windows, Android Studio JBR 21, SDK de compilación 36, emulador Pixel 3a x86_64 con API 37. El proyecto conserva minSdk 26 y targetSdk 36.

## Resultados comprobados

- `assembleDebug`: correcto.
- `testDebugUnitTest`: 4 pruebas correctas (incluye la prueba de plantilla).
- `connectedDebugAndroidTest`: 10 pruebas correctas en tamaño compacto, y 10 correctas con tamaño expandido simulado (1600 x 1200, densidad 160). Las dimensiones se restauraron al finalizar.
- Las 3 pruebas de interfaz también pasaron en tamaño medio simulado (1200 x 1600, densidad 240, ancho de 800 dp).
- `lintDebug`: correcto, sin errores bloqueantes. Conserva advertencias por versiones más recientes de herramientas/dependencias, recursos de plantilla sin uso, ubicación del logo y sugerencias de estilo. No se actualiza el entorno de forma innecesaria solo para silenciar esas advertencias.
- Login de estudiante, docente y UTP; práctica correcta/incorrecta, prueba enviada, revisión y acceso a gestión: comprobados mediante pruebas Compose.
- Persistencia al cerrar y reabrir la base, límite de intentos, validaciones, fechas, bloqueo por rol, publicación y deshabilitación: comprobados con SQLite real en una base de pruebas separada.
- Cámara del emulador: abrir, capturar, aceptar, ver vista previa y guardar copia JPEG privada: comprobado.
- Galería del emulador: abrir selector del sistema, seleccionar imagen de prueba, confirmar y guardar: comprobado.
- Cancelación de captura: regresa sin fallos y conserva la imagen guardada: comprobado.
- Capturas de inicio, unidad, prueba y paneles: inspeccionadas visualmente. No se usaron personas reales.

Las primeras ampliaciones de la prueba de UI fallaron por buscar el texto exacto de una alternativa sin su símbolo de selección. Se corrigió el selector y la sincronización con el guardado; la ejecución completa posterior terminó sin fallos. No se ocultan ni se convierten en aprobadas pruebas fallidas.

## Pendiente de comprobación por el equipo

- Cámara y galería en teléfono físico, como pide la guía 13. Probar además orientación de fotos y cancelaciones en ese dispositivo.
- Abrir el informe TXT desde una aplicación externa después de descargarlo; el texto generado y la autorización docente sí se comprobaron en el repositorio.
- Escuchar y validar el audio con altavoces del dispositivo objetivo. La reproducción usa un WAV local; no se verificó audibilidad desde el equipo anfitrión.
- Validación pedagógica de objetivos y materiales, aceptación de cámara/galería como adaptación del caso, integrantes/número de equipo y evidencias reales de Trello.
- Firma release bajo control del equipo. El APK entregado es debug, apto para pruebas.

La aplicación no implementa sincronización remota ni backend. Las pruebas no certifican seguridad de producción ni equivalencia completa con el sistema institucional descrito como evolución del caso.

## Ampliación de actividades por asignatura

- Ocho unidades y 42 preguntas totales; cuatro unidades nuevas con 14 prácticas y 12 preguntas de prueba.
- Cinco pruebas unitarias aprobadas, incluyendo validación numérica y orden de pasos.
- Doce pruebas en emulador aprobadas, incluyendo escritura de resultado numérico, orden de un relato y migración real desde el esquema SQLite v1 sin perder respuestas/intent os.
- Compilación y lint correctos. Las comprobaciones anteriores de tamaños y recursos nativos corresponden a la entrega base; la ampliación se ejecutó en el tamaño compacto del mismo Pixel 3a.

## Indicadores y finalización

- Doce pruebas de emulador y cinco unitarias correctas; después se ampliaron y aprobaron las cuatro pruebas de interfaz con la finalización de los cuatro retos numéricos.
- Se comprobó la posición de cada pregunta, cierre de práctica, cierre de prueba, diferencia entre respondidas/acertadas, consulta del resultado anterior y nuevo intento con elecciones vacías.
- La primera ejecución detectó un fallo de presentación al reiniciar el desplazamiento de una pregunta. Se eliminó ese reinicio imperativo y cada pregunta crea su área de contenido con key; el recorrido completo posterior terminó correctamente.
- La prueba de contexto se adaptó al identificador separado de validación. Las pruebas automáticas se ejecutaron con mpazIsolatedTests=true; no se desinstaló la aplicación habitual del usuario.
- Se compararon intentos, respuestas de práctica y respuestas del examen de la instalación habitual antes/después de verificar: idénticos. El intento del usuario se conservó.
- No se cambió el esquema SQLite ni el contenido de las preguntas en esta modificación.

## Identidad Explora Marcela Paz

- Compilación, cinco pruebas unitarias, doce pruebas de emulador y lint correctos al aplicar el boceto.
- Se añadió una prueba de controles del avatar (aumentar/reducir texto y salir). Las cinco pruebas de interfaz pasaron en teléfono y en ancho expandido simulado.
- Se inspeccionó una captura real del inicio con el nuevo logo, ilustración, textos interactivos y cuatro asignaturas; no se usa el boceto como una pantalla falsa.
- El nombre visible, icono, acceso, audio e informes utilizan Explora Marcela Paz. Se conserva el applicationId y el esquema SQLite para actualizar la instalación habitual sin borrar los datos.
- Las pruebas se ejecutaron en cl.duoc.m.validation; no reemplazaron la instalación que contiene el examen del usuario.
- Después de instalar el APK normal actualizado, se compararon las tablas de práctica, intentos y respuestas con la copia previa: idénticas. El examen del usuario se conservó y se abrió el nuevo inicio en la instalación habitual.

## Perfiles propios

- Catorce pruebas de emulador, cinco unitarias y lint correctos.
- Se verificó el perfil de estudiante (curso, docente, ID ficticio y restricciones), docente (cuenta propia aun tras seleccionar un alumno) y UTP (área pedagógica).
- Se comprobaron los ajustes de texto y la salida de sesión desde la nueva pantalla de perfil.
- Los perfiles son de consulta; no modifican las cuentas ni agregan tablas SQLite. Las pruebas continuaron usando el identificador aislado de validación.
- Tras actualizar la instalación habitual se abrió Mi perfil del estudiante y se compararon sus registros de aprendizaje con la copia anterior: respuestas e intentos conservados.

## Login y simplificación de textos

Se rediseñó el acceso con los recursos existentes y se redujeron los avisos repetidos en perfiles, actividades y paneles. La batería de 24 pruebas pasó durante la modificación; tras compactar la presentación se repitieron correctamente el login y el recorrido de práctica/prueba (dos pruebas), y después el login en tamaño expandido (una prueba). El ajuste final de la ilustración se inspeccionó en una captura real del login compacto y en la captura expandida. Compilación debug y lint finales correctos.

Se instaló el APK normal con actualización conservando datos. La huella SHA-256 de la base habitual antes y después coincidió. Se restauró el tamaño del emulador y se dejó abierto el nuevo login. No se agregaron dependencias ni se cambió el esquema SQLite.

## Trabajos y cursos

Ampliación del 2 de noviembre de 2026 (fecha de Chile): SQLite v3, cursos separados, trabajos asignados a estudiantes del curso, borradores y entregas de texto, corrección provisional y publicación de nota/comentario.

- Treinta pruebas de emulador aprobadas en la primera batería completa, incluidas cuatro de WorkRepository y dos recorridos de WorkFlow.
- Ocho pruebas unitarias aprobadas: validación de notas, fecha, respuesta y publicación, además de las anteriores.
- Dos recorridos de trabajos aprobados en pantalla expandida simulada (1600 × 1200, densidad 160), restaurando después la configuración original.
- Tras ajustar colores, teclado y posición de la evaluación, se repitieron y aprobaron los trece recorridos de interfaz en teléfono (SchoolFlow y WorkFlow), las ocho pruebas unitarias y lint.
- La primera compilación detectó un import faltante de dp en StaffScreens; se corrigió antes de ejecutar las baterías. Las ejecuciones de pruebas de esta ampliación terminaron sin fallos.
- Se verificaron persistencia de borrador al cambiar de sesión, ocultamiento de la corrección provisional, publicación y consulta de nota 6,5, separación entre cursos y bloqueo de entregas ya enviadas.
- La migración desde esquema v2 se comprobó conservando respuestas de práctica, intento, respuestas de examen, observación y ruta de avatar. La prueba anterior desde v1 continuó aprobada.
- Se inspeccionaron capturas reales de cursos, creación, entregas, corrección docente y nota/comentario del estudiante.

No se incorporan archivos adjuntos ni sincronización. Las reglas, límites y relación con las guías están en TRABAJOS_Y_CURSOS.md.

Actualización de la instalación habitual: se abrió la cuenta docente y se comprobó la migración real a v3. integrity_check devolvió ok. Se compararon con la copia previa las filas completas de práctica, intentos, respuestas de examen, observaciones, unidades, preguntas, auditoría, usuarios anteriores y sus asignaciones: idénticas. La cuenta nueva de Mateo y sus asignaciones son las únicas incorporaciones a los datos de demostración anteriores. Se dejó abierto Mis cursos. No se copiaron los trabajos ni las notas de las pruebas a la instalación habitual.
