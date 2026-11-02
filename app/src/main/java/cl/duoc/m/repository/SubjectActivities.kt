package cl.duoc.m.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import cl.duoc.m.model.ActivityKind

// Actividades originales. Amplían el contenido sin reemplazar el historial anterior.
object SubjectActivities {
    private data class Task(
        val prompt: String, val options: List<String>, val correct: Int,
        val explanation: String, val kind: ActivityKind = ActivityKind.SELECCION
    )
    private data class UnitContent(
        val subject: String, val title: String, val objective: String,
        val explanation: String, val example: String,
        val practice: List<Task>, val test: List<Task>
    )
    private fun choice(prompt: String, a: String, b: String, c: String, correct: Int, explanation: String) =
        Task(prompt, listOf(a,b,c), correct, explanation)
    private fun number(prompt: String, answer: String, explanation: String) =
        Task(prompt, listOf(answer,"No corresponde","Sin respuesta"), 0, explanation, ActivityKind.NUMERO)
    private fun order(prompt: String, a: String, b: String, c: String, explanation: String) =
        Task(prompt, listOf("$a → $b → $c", "$c → $a → $b", "$b → $c → $a"), 0, explanation, ActivityKind.ORDEN)

    fun insert(db: SQLiteDatabase) {
        val units = listOf(
            UnitContent("Lenguaje", "La biblioteca viajera",
                "Ordenar hechos de un relato, reconocer información y explicar una acción a partir de pistas",
                "El lunes, Luna encontró una caja de libros en su sala. Quiso que todo el curso pudiera leerlos. Primero limpió un estante. Después ordenó los libros por tema. Finalmente preparó una lista de préstamos. Al día siguiente, Sol devolvió un libro con una hoja suelta. Luna lo reparó con ayuda de su docente antes de prestarlo otra vez.",
                "Busca pistas como primero, después y finalmente. Para explicar por qué alguien actúa, relaciona su acción con lo que quiere lograr. Luna prepara préstamos porque quiere compartir los libros.",
                listOf(
                    order("Ordena cómo organizó Luna la biblioteca. Toca los pasos del primero al último.", "Limpió el estante", "Ordenó los libros", "Preparó la lista", "El relato presenta esas acciones con las pistas primero, después y finalmente."),
                    choice("¿Qué título representa mejor lo que sucede en todo el relato?", "Una biblioteca para compartir", "Un viaje a la playa", "El libro desaparecido",0,"Luna organiza y cuida libros para que su curso pueda leerlos."),
                    choice("¿Por qué Luna reparó el libro antes de volver a prestarlo?", "Para que nadie pudiera leerlo", "Para que otra persona lo recibiera en buen estado", "Para cambiar el tema del libro",1,"El libro tenía una hoja suelta. Repararlo permite seguir compartiéndolo en buen estado.")
                ),
                listOf(
                    choice("¿Qué hizo Luna inmediatamente después de limpiar el estante?", "Preparó la lista", "Ordenó los libros por tema", "Reparó una hoja",1,"Después de limpiar, ordenó los libros. Luego preparó la lista."),
                    choice("¿Qué problema tuvo el libro que devolvió Sol?", "Tenía una hoja suelta", "Estaba vacío", "No tenía dibujos",0,"El relato dice que Sol devolvió un libro con una hoja suelta."),
                    choice("¿Qué acción sigue el propósito de la biblioteca de Luna?", "Ocultar los libros", "Prestar sin registrar y no devolver", "Registrar el préstamo y devolver el libro cuidado",2,"Compartir, registrar y cuidar los libros permite que el curso siga usándolos.")
                )),
            UnitContent("Matemática", "Resolvemos en la feria",
                "Usar decenas y unidades, resolver problemas aditivos y completar secuencias hasta 100",
                "En una feria puedes reunir, vender y ordenar productos. Reunir cantidades se representa con una suma. Si se vende una parte, una resta indica lo que queda. Un número tiene decenas y unidades: 46 tiene 4 decenas y 6 unidades. En una secuencia, busca cuánto aumenta o disminuye cada paso.",
                "Hay 23 manzanas y llegan 14: 20 + 10 = 30 y 3 + 4 = 7; en total 37. Si se venden 5 de esas 37, quedan 32. Para comprobar una resta puedes sumar: 32 + 5 = 37.",
                listOf(
                    number("Valor posicional: ¿qué número forman 4 decenas y 6 unidades? Escribe solo el número.","46","4 decenas son 40. Al sumar 6 unidades obtenemos 46."),
                    number("Llegaron 27 peras y después 12 más. ¿Cuántas peras hay ahora?","39","Reunimos: 27 + 12. Las decenas dan 30 y las unidades 9. Resultado: 39."),
                    number("En la caja había 45 naranjas. Se vendieron 13. ¿Cuántas quedaron?","32","Quitamos lo vendido: 45 - 13 = 32. Comprueba: 32 + 13 = 45."),
                    number("Completa la secuencia: 20, 25, 30, __, 40.","35","Cada número aumenta en 5. Después de 30 viene 35.")
                ),
                listOf(
                    choice("¿Qué número tiene 5 decenas y 2 unidades?","25","52","502",1,"5 decenas son 50; 50 + 2 = 52."),
                    choice("Hay 36 tomates y llegan 21. ¿Qué operación y resultado corresponden?","36 - 21 = 15","36 + 21 = 57","36 + 21 = 47",1,"Llegan más tomates: hay que sumar. 30 + 20 = 50 y 6 + 1 = 7."),
                    choice("Había 68 huevos y se vendieron 24. ¿Cuántos quedan?","44","92","54",0,"68 - 24 = 44; 44 + 24 = 68 permite comprobarlo.")
                )),
            UnitContent("Ciencias Naturales", "Observamos el agua",
                "Distinguir estados del agua y relacionar cambios con enfriamiento y calentamiento",
                "El agua puede encontrarse como hielo sólido, agua líquida o vapor de agua gaseoso. Al recibir calor, el hielo se derrite y pasa a líquido. Al enfriarse lo suficiente, el agua líquida puede congelarse. Cuando el agua se evapora pasa a gas. Observa sin tocar agua caliente: estas experiencias requieren supervisión adulta.",
                "Una cubetera contiene agua líquida. Una persona adulta la pone en el congelador. Después de suficiente tiempo se forman cubos de hielo. Si dejamos un cubo en un plato a temperatura ambiente, se derrite. Cambia el estado, pero sigue siendo agua.",
                listOf(
                    choice("Clasifica: un cubo de hielo conserva su forma. ¿En qué estado está?","Sólido","Líquido","Gaseoso",0,"El hielo es agua en estado sólido y mantiene una forma propia."),
                    order("Ordena esta experiencia de enfriamiento.","Llenar la cubetera con agua", "Ponerla en el congelador", "Observar cubos de hielo", "Primero hay agua líquida. Al enfriarse durante suficiente tiempo se forma hielo."),
                    choice("Dos cubos iguales se dejan uno al sol y otro a la sombra. ¿Qué comparación es adecuada?","Usar cubos de distinto tamaño y observar uno solo", "Cambiar el lugar, mantener el tamaño y observar ambos", "Afirmar el resultado sin observar",1,"Cambiar solo el lugar y usar cubos iguales ayuda a comparar qué ocurre. La observación permite sacar conclusiones."),
                    choice("Predice: dejamos un cubo en un plato a temperatura ambiente. ¿Qué esperamos observar?","Que se transforme en una piedra", "Que aumente sin recibir agua", "Que se derrita y aparezca agua líquida",2,"Al recibir calor del ambiente, el hielo pasa de sólido a líquido.")
                ),
                listOf(
                    choice("Una botella contiene agua que toma la forma del recipiente. ¿Qué estado se describe?","Sólido","Líquido","Gaseoso",1,"El agua líquida fluye y toma la forma del recipiente."),
                    choice("¿Qué cambio ocurre al congelar agua líquida?","Pasa de líquido a sólido", "Deja de ser agua", "Pasa de sólido a gas",0,"Al enfriarse lo suficiente, el agua líquida se convierte en hielo sólido."),
                    choice("¿Qué harías para observar un cambio con seguridad?","Tocar una olla caliente", "Calentar agua sin supervisión", "Observar cómo se derrite un hielo en un plato",2,"El hielo en un plato permite observar un cambio sin manipular una fuente de calor.")
                )),
            UnitContent("Historia y Geografía", "Un plano para nuestra comunidad",
                "Leer un plano sencillo, usar puntos cardinales y proponer acuerdos de convivencia",
                "Este plano se lee desde arriba. El norte está arriba; el sur abajo; el oeste a la izquierda y el este a la derecha. Cada casilla es un lugar. Para moverte, pasa entre casillas que comparten un lado. La plaza está en el centro. Las comunidades también necesitan acuerdos para compartir sus espacios.",
                "PLANO: norte ↑\nBiblioteca | Escuela | Consultorio\nPanadería | Plaza | Bomberos\nCancha | Paradero | Huerto\n\nEjemplo: desde la plaza, una casilla al norte lleva a la escuela. Al compartir una biblioteca, respetamos turnos y cuidamos los libros.",
                listOf(
                    choice("Ubícate en la plaza del plano. ¿Qué lugar está una casilla al este?","Panadería","Bomberos","Paradero",1,"El este está a la derecha. Bomberos está a la derecha de la plaza."),
                    choice("Desde la escuela avanzas una casilla al sur y luego una al oeste. ¿A dónde llegas?","Panadería","Consultorio","Huerto",0,"Desde la escuela, sur lleva a la plaza y oeste lleva a la panadería."),
                    choice("En la biblioteca dos personas quieren el mismo libro. ¿Qué acuerdo permite compartirlo?","Quedárselo sin avisar", "Impedir que otra persona lea", "Acordar turnos de préstamo y devolución",2,"Un acuerdo de turnos permite que ambas personas lean y cuida un recurso común.")
                ),
                listOf(
                    choice("Según el plano, ¿qué lugar queda una casilla al sur de la plaza?","Escuela","Paradero","Biblioteca",1,"El sur está abajo. El paradero está debajo de la plaza."),
                    choice("¿Qué recorrido lleva de la plaza al consultorio usando dos pasos?","Norte y después este", "Sur y después oeste", "Oeste y después sur",0,"Norte lleva a la escuela; desde allí, este lleva al consultorio."),
                    choice("¿Qué acción cuida un espacio común como la plaza?","Dejar basura para que otra persona la recoja", "Usar todos los juegos sin dar turnos", "Respetar turnos y depositar los residuos en un basurero",2,"Respetar a otras personas y cuidar la limpieza beneficia a la comunidad.")
                ))
        )
        units.forEach { unit ->
            val lessonId = db.insertOrThrow("lessons",null,ContentValues().apply {
                put("subject",unit.subject); put("title",unit.title); put("objective",unit.objective)
                put("explanation",unit.explanation); put("example",unit.example); put("status","PUBLICADO")
                put("origin","Actividad original de refuerzo para Segundo Básico. Validar su adecuación pedagógica con el docente.")
            })
            listOf(false to unit.practice, true to unit.test).forEach { (finalTest,tasks) ->
                tasks.forEach { task -> db.execSQL(
                    "INSERT INTO questions(lesson_id,prompt,a,b,c,correct,explanation,final_test,kind) VALUES(?,?,?,?,?,?,?,?,?)",
                    arrayOf(lessonId,task.prompt,task.options[0],task.options[1],task.options[2],task.correct,task.explanation,if(finalTest) 1 else 0,task.kind.name)
                ) }
            }
            listOf(3,4).forEach {student -> db.execSQL("INSERT INTO assignments VALUES(?,?,?,?,?)",arrayOf(student,lessonId,2,"2026-01-01","2030-12-31"))}
        }
        db.execSQL("INSERT INTO audit(date,actor,action) VALUES(datetime('now','localtime'),'Sistema','Añadidas cuatro unidades con actividades específicas por asignatura; se conserva el progreso previo')")
    }
}
