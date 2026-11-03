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

Los archivos PNG mantienen su transparencia y se integran como recursos de Android. La interfaz se construye con componentes Compose; las capturas de verificación proceden del emulador.
