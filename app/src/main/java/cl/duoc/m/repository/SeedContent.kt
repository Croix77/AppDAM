package cl.duoc.m.repository

import android.database.sqlite.SQLiteDatabase

// Textos y ejercicios originales. No se atribuyen códigos OA oficiales sin validación.
object SeedContent {
    private data class Seed(val subject: String, val title: String, val objective: String, val text: String, val example: String, val questions: List<List<String>>)
    fun insert(db: SQLiteDatabase) {
        val seeds = listOf(
            Seed("Lenguaje", "Una visita al huerto", "Comprender un relato breve e identificar información explícita",
                "Sol visitó el huerto de la escuela con su curso. Vio tomates rojos y lechugas verdes. La profesora explicó que las plantas necesitan agua y luz. Antes de volver a la sala, Sol regó una lechuga con cuidado.",
                "Para saber dónde ocurre una historia, busca el lugar nombrado. En este relato, el curso visita el huerto de la escuela.",
                listOf(listOf("¿Dónde estuvo Sol?", "En el huerto", "En la playa", "En el cine", "0", "El relato dice que Sol visitó el huerto de la escuela."),
                    listOf("¿Qué hizo antes de volver a la sala?", "Corrió al patio", "Regó una lechuga", "Compró tomates", "1", "La última oración cuenta que Sol regó una lechuga."),
                    listOf("¿De qué color eran los tomates?", "Azules", "Verdes", "Rojos", "2", "El texto describe tomates rojos."),
                    listOf("¿Qué necesitan las plantas según el relato?", "Solo tierra", "Agua y luz", "Pintura", "1", "La profesora explicó que necesitan agua y luz."))),
            Seed("Matemática", "Contamos en el huerto", "Resolver sumas y restas con números hasta 100",
                "Sumar es reunir cantidades. Restar es quitar o encontrar cuánto queda. Puedes separar decenas y unidades. Lee qué pasa antes de elegir una operación.",
                "Hay 24 semillas y llegan 13 más: 20 + 10 = 30; 4 + 3 = 7. En total hay 37 semillas. Si de 37 plantas se retiran 5, quedan 32.",
                listOf(listOf("Hay 12 tomates y recoges 6 más. ¿Cuántos tienes?", "18", "6", "16", "0", "12 + 6 = 18 porque reunimos las cantidades."),
                    listOf("De 25 semillas plantas 4. ¿Cuántas quedan?", "29", "21", "20", "1", "25 - 4 = 21 porque quitamos cuatro semillas."),
                    listOf("¿Cuánto es 32 + 15?", "37", "57", "47", "2", "30 + 10 = 40 y 2 + 5 = 7: en total 47."),
                    listOf("Había 48 hojas y cayeron 6. ¿Cuántas quedan?", "42", "54", "44", "0", "48 - 6 = 42."))),
            Seed("Ciencias Naturales", "El ciclo de una planta", "Reconocer necesidades y etapas de crecimiento de las plantas",
                "Una semilla puede germinar si encuentra condiciones adecuadas. Aparece una raíz y luego un tallo con hojas. La raíz absorbe agua; las hojas reciben luz. Cuidamos una planta con agua suficiente, luz y espacio.",
                "Orden guiado: semilla → germinación → planta con hojas. Una planta junto a una ventana recibe luz. Regarla no significa dejarla siempre inundada.",
                listOf(listOf("¿Qué parte absorbe agua?", "La raíz", "La flor", "El fruto", "0", "La raíz toma agua del suelo."),
                    listOf("¿Cuál es el orden correcto?", "Planta, semilla, raíz", "Semilla, germinación, planta", "Flor, fruto, semilla siempre", "1", "Primero está la semilla, luego germina y crece la planta."),
                    listOf("¿Qué necesita una planta para crecer?", "Solo oscuridad", "Agua, luz y espacio", "Agua sin límite", "1", "Las plantas necesitan condiciones adecuadas de agua, luz y espacio."),
                    listOf("¿Qué cuidado es adecuado?", "Arrancar sus hojas", "Taparla sin luz", "Darle agua suficiente", "2", "Regamos con cuidado, sin inundar la planta."))),
            Seed("Historia y Geografía", "Nos ubicamos y convivimos", "Usar referencias espaciales y reconocer acuerdos de convivencia",
                "Un plano representa un lugar visto desde arriba. Usamos referencias como cerca, lejos, delante y detrás para ubicarnos. En la escuela compartimos espacios: respetar turnos y cuidar los materiales ayuda a convivir.",
                "En nuestro ejemplo, la biblioteca está al lado de la sala y el patio está frente a ella. Para pedir un libro, esperamos nuestro turno y lo devolvemos en buen estado.",
                listOf(listOf("¿Cómo representa un lugar un plano?", "Visto desde arriba", "Como un sonido", "Solo de noche", "0", "Un plano muestra un lugar visto desde arriba."),
                    listOf("¿Qué ayuda a convivir en la biblioteca?", "Gritar", "Respetar los turnos", "Esconder los libros", "1", "Respetar turnos permite que todas las personas participen."),
                    listOf("En el ejemplo, ¿qué está al lado de la sala?", "La playa", "La biblioteca", "El aeropuerto", "1", "La biblioteca está al lado de la sala."),
                    listOf("¿Qué hacemos con un libro prestado?", "Lo devolvemos cuidado", "Lo rompemos", "Lo escondemos", "0", "Cuidar y devolver materiales es un acuerdo de convivencia.")))
        )
        seeds.forEachIndexed { index, s ->
            val id = index + 1
            db.execSQL("INSERT INTO lessons(id,subject,title,objective,explanation,example,status,origin) VALUES(?,?,?,?,?,?,?,?)", arrayOf(id,s.subject,s.title,s.objective,s.text,s.example,"PUBLICADO","Material original del equipo para demostración. OA descriptivo pendiente de validación docente."))
            s.questions.forEachIndexed { qIndex, q ->
                db.execSQL("INSERT INTO questions(lesson_id,prompt,a,b,c,correct,explanation,final_test) VALUES(?,?,?,?,?,?,?,?)", arrayOf(id,q[0],q[1],q[2],q[3],q[4].toInt(),q[5],if(qIndex < 2) 0 else 1))
            }
            listOf(3,4).forEach { student -> db.execSQL("INSERT INTO assignments VALUES(?,?,?,?,?)", arrayOf(student,id,2,"2026-01-01","2030-12-31")) }
        }
        db.execSQL("INSERT INTO audit(date,actor,action) VALUES(datetime('now','localtime'),'Sistema','Carga de demostración: cuatro unidades autorizadas, publicadas y asignadas a usuarios ficticios')")
    }
}
