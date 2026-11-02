# Muestra visual: cuaderno de exploración

Alcance aprobado: inicio del estudiante y práctica numérica de la unidad Resolvemos en la feria. La lectura, las pruebas finales, las demás asignaturas y los paneles de gestión conservan su presentación actual hasta revisar esta muestra.

## Inicio

- Avatar de 44 dp y saludo compacto; se conservan bienvenida, logo y Toast de Comenzar.
- Tarjeta principal para una unidad en curso o una unidad por comenzar. El porcentaje se calcula con los registros locales; no es un dato simulado.
- Cuatro accesos con nombre y color: violeta/Lenguaje, azul/Matemática, verde/Ciencias y terracota/Historia. Tocar una asignatura filtra la lista y Ver todas restaura las unidades.
- Cada unidad indica Por comenzar, En curso o Completada. Es estado de avance individual, no una métrica consolidada docente.
- Dos columnas de accesos en teléfono y cuatro en ancho expandido. Los elementos permiten desplazamiento y el contenido tiene ancho máximo.

## Matemática

- Un reto a la vez, con posición e indicador de retos resueltos.
- Pistas visuales de decenas/unidades, suma, resta y secuencia, obtenidas de los números de la pregunta. No muestran el resultado antes de responder.
- Respuesta de texto numérica, comprobación y explicación. El teclado se cierra al comprobar.
- Una respuesta incorrecta mantiene el reto y permite corregir; Siguiente reto se habilita tras resolverlo correctamente. Anterior permite revisar lo guardado.
- La unidad se abre en el primer ejercicio sin resolver; si todos se completaron, comienza en el primero para revisar. El botón final vuelve a la unidad para consultar el avance y acceder a la prueba.
- La selección del reto y los borradores usan rememberSaveable. Respuestas correctas e intentos siguen guardándose mediante el ViewModel y SQLite existentes.

## Archivos para estudiar

- HomeScreen.kt: composición del inicio, selección de asignatura y estados visuales.
- ExplorationStyle.kt: una data class y un when con la identidad de cada asignatura.
- MathPracticeScreen.kt: Column, Row, Surface, Text, TextField, Button y estado de una pregunta; no introduce librerías nuevas.
- SchoolViewModel.lessonProgress: consulta el progreso existente de cada unidad para mostrarlo en el inicio.

Los cambios reutilizan las técnicas de las guías: componentes, Modifier, Material 3, estado guardable, ViewModel, StateFlow y AnimatedVisibility. Los signos y letras son marcas visuales construidas con Text y Surface, no imágenes de terceros ni un sistema nuevo de iconos.

La muestra se entrega en una rama de revisión. No se ha extendido el estilo al resto de las pantallas ni se cambió el esquema SQLite.

## Verificación de esta muestra

- Compilación, cinco pruebas unitarias, doce pruebas de emulador y lint: correctos en la primera versión de la muestra.
- Tras compactar la portada: las cuatro pruebas de interfaz pasaron en teléfono.
- Filtro por asignatura, restauración de todas las unidades, respuesta matemática incorrecta/correcta, Siguiente reto y revisión del reto anterior: comprobados también en ancho medio (800 dp).
- Capturas reales inspeccionadas del inicio y del primer reto numérico. El resto de las comprobaciones históricas del proyecto se mantiene documentado en VERIFICACION.md.
