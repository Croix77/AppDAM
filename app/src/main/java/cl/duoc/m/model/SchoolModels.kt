package cl.duoc.m.model

// Modelos simples: describen datos, sin dibujar pantallas ni consultar SQLite.
enum class Role { ESTUDIANTE, DOCENTE, UTP }
enum class UnitStatus { BORRADOR, EN_REVISION, AUTORIZADO, PUBLICADO, DESHABILITADO }
data class User(val id: Long, val username: String, val name: String, val role: Role, val course: String, val teacherId: Long?, val photo: String = "")
data class Lesson(
    val id: Long, val subject: String, val title: String, val objective: String,
    val explanation: String, val example: String, val status: UnitStatus,
    val origin: String = "Contenido original de demostración; validar OA con el docente"
)
data class Question(val id: Long, val lessonId: Long, val prompt: String, val options: List<String>, val correct: Int, val explanation: String, val finalTest: Boolean)
data class Assignment(val studentId: Long, val lessonId: Long, val maxAttempts: Int, val start: String, val end: String)
data class Answer(val questionId: Long, val choice: Int, val correct: Boolean)
data class Attempt(val id: Long, val studentId: Long, val lessonId: Long, val number: Int, val score: Int, val total: Int, val date: String)
data class Report(val student: User, val lesson: Lesson, val attempts: List<Attempt>, val answers: List<Pair<Question, Answer>>, val note: String, val validated: Boolean, val progress: Int)
data class AuditEntry(val date: String, val actor: String, val action: String)

object FormRules {
    fun login(username: String, password: String): Map<String, String> = buildMap {
        if (username.isBlank()) put("username", "Escribe tu usuario")
        if (password.isBlank()) put("password", "Escribe tu clave")
    }
    fun assignment(attempts: String, start: String, end: String): String? {
        if (attempts.toIntOrNull() !in 1..5) return "Los intentos deben estar entre 1 y 5"
        return try {
            val first = java.time.LocalDate.parse(start)
            val last = java.time.LocalDate.parse(end)
            if (last.isBefore(first)) "La fecha final no puede ser anterior a la inicial" else null
        } catch (_: Exception) { "Usa fechas válidas con formato AAAA-MM-DD" }
    }
    fun question(prompt: String, options: List<String>, explanation: String): String? = when {
        prompt.isBlank() -> "Escribe la pregunta"
        options.size != 3 || options.any { it.isBlank() } -> "Completa las tres alternativas"
        options.map { it.trim().lowercase() }.distinct().size != 3 -> "Las alternativas deben ser diferentes"
        explanation.isBlank() -> "Explica la respuesta correcta"
        else -> null
    }
}
