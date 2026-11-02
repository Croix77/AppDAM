# Explora Marcela Paz: aplicación del boceto aprobado

El nombre visible de la aplicación es Explora Marcela Paz, con el lema Aprende a tu ritmo y contenidos de Segundo Básico. El caso académico de referencia conserva su título original MPAZ PRO 2.º y corresponde a la Escuela Escritora Marcela Paz. La identidad propuesta para el prototipo no se presenta como escudo oficial de la escuela.

## Composición implementada

- Cabecera: libro/camino/estrella, Explora y Marcela Paz, más avatar del estudiante.
- Saludo breve y tarjeta verde con el título Tu próximo descubrimiento, unidad recomendada real, ilustración de libro/camino y botón Continuar aprendiendo.
- Cuatro accesos pastel: libro violeta, operaciones azules, hojas verdes y globo terracota. Al tocar uno se abre su lista de unidades con los estados reales de progreso.
- Pie con confirmación de guardado automático. El guardado continúa siendo local, sin sincronización de nube.
- Al pulsar el avatar o su etiqueta Mi perfil se abre el perfil propio con información escolar, controles de tamaño de texto y Salir. No permite editar la imagen del estudiante. Docente y UTP también disponen de un perfil propio, descrito en PERFILES_DE_CUENTA.md.
- Nombre de Android, icono del lanzador, entrada, audio e informes con la nueva identidad. Se retiró el recurso anterior logo_mpaz.png.

Se conserva el applicationId cl.duoc.m, la base de datos y los identificadores existentes para que actualizar la aplicación no borre el progreso. La prueba automatizada usa un identificador separado.

## Relación con el material de estudio

La composición usa Image, Text, Box, Column, Row, Button, Surface/Card y Modifier de la base Compose/Material 3. Las ilustraciones no se dibujan con algoritmos: son recursos de drawable. La navegación conserva NavHost y un ViewModel compartido, como la guía 11. La asignatura seleccionada es una propiedad de SchoolUiState, no una ruta con serialización avanzada. No se añadieron librerías.

Los iconos son vectores de Android guardados como recursos XML y mostrados con Image. No es necesario memorizar sus coordenadas para explicar la pantalla: basta reconocer que son recursos gráficos. Para adaptarse a anchos grandes se mantienen Window Size Classes y cuatro accesos en fila; teléfono usa una cuadrícula de dos por dos. Los textos de unidades/resultados proceden de SQLite.

## Recursos y preparación

- app/src/main/res/drawable-nodpi/explora_logo.png: logo con transparencia.
- app/src/main/res/drawable-nodpi/explora_hero.png: ilustración con transparencia de libro, camino, colinas y estrella.
- app/src/main/res/drawable/subject_*.xml: iconos originales del proyecto.
- BrandLockup.kt: imagen del logo y nombre en texto real.
- HomeScreen.kt: composición del inicio y lista de unidades por asignatura.

Las dos ilustraciones se prepararon con la herramienta integrada de generación/edición de imágenes, usando como referencia el boceto visual aprobado. Después se exportaron a tamaños apropiados para Android conservando transparencia. No se generó una captura falsa para mostrarla como aplicación ejecutable: las capturas de verificación se obtienen del emulador.

### Prompts finales de preparación de imágenes

**Logo (edición de la extracción inicial):**

> Edit this logo asset only: REMOVE ALL GLOW, luminous halos, blur, gradients and shadows from around every shape. Preserve the exact open-book silhouette, page shapes, winding path and star. Use flat opaque solid fills inside the shapes: deep teal book outline/path, violet and blue left pages, green and terracotta right pages, solid gold star and rays. Outside these crisp flat shapes alpha must be fully transparent with no colored haze. It must look like a clean flat vector logo printed on paper, not neon or a glowing sign. No words, no background, no other changes. Tight framing with small even transparent margin.

**Ilustración de tarjeta (edición desde el boceto):**

> Edit target: approved brand proposal board. Reproduce the exact decorative illustration INSIDE the dark teal hero card on the phone: open cream-colored book at bottom right with blue-teal outer binding, pale winding path rising from its center into layered green hills and a gold star with two short gold rays above. Output ONLY a standalone horizontal illustration asset. Transparent background. Keep the book and hills on the far right 45 percent, leave left 55 percent fully transparent for live interface text. Preserve the illustration's original colors, clean flat layered paper illustration style and composition. Aspect ratio about 2:1, book meets bottom and right edges, gold star near upper-right. Exclude the phone, card background, words, buttons, logos and all other elements. This is an asset for layering over a solid dark teal Compose card, not a rendered mockup.

Modo utilizado: herramienta integrada image_gen; salida transparente. Las ilustraciones son recursos preparados con asistencia generativa; los textos pedagógicos siguen siendo los ejercicios originales documentados en el proyecto.
