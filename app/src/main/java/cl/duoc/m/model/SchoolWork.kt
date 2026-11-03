package cl.duoc.m.model

import java.time.LocalDate

data class SchoolWork(val id: Long,val teacherId: Long,val course: String,val subject: String,val title: String,val instructions: String,val dueDate: String,val assigned: Int,val delivered: Int,val reviewed: Int)
data class WorkSubmission(val workId: Long,val studentId: Long,val response: String,val submittedAt: String,val gradeTenths: Int?,val feedback: String,val published: Boolean,val reviewedAt: String) {
    val delivered get()=submittedAt.isNotBlank()
    val status get()=when {published -> "Revisado";delivered -> "Entregado";else -> "Pendiente"}
    val gradeLabel get()=gradeTenths?.let {"${it/10},${it%10}"}.orEmpty()
}
data class WorkStudent(val student: User,val submission: WorkSubmission?)

object WorkRules {
    val subjects=listOf("Lenguaje","Matemática","Ciencias Naturales","Historia y Geografía")
    fun creation(title: String,instructions: String,due: String): Map<String,String> = buildMap {
        if(title.isBlank()) put("title","Escribe un título") else if(title.trim().length>100) put("title","Usa hasta 100 caracteres")
        if(instructions.isBlank()) put("instructions","Escribe las instrucciones") else if(instructions.trim().length>4000) put("instructions","Usa hasta 4000 caracteres")
        val date=runCatching {LocalDate.parse(due)}.getOrNull()
        if(date==null) put("due","Usa una fecha válida: AAAA-MM-DD") else if(date<LocalDate.now()) put("due","Elige hoy o una fecha posterior")
    }
    fun response(value: String): String?=when {value.isBlank() -> "La respuesta no puede quedar vacía";value.trim().length>4000 -> "Usa hasta 4000 caracteres";else -> null}
    fun gradeTenths(value: String): Int? {
        val text=value.trim()
        if(!text.matches(Regex("[1-7]([.,][0-9])?"))) return null
        val parts=text.replace(',','.').split('.')
        val grade=parts[0].toInt()*10+(parts.getOrNull(1)?.toInt() ?: 0)
        return grade.takeIf {it in 10..70}
    }
    fun review(grade: String,feedback: String,publish: Boolean): Map<String,String> = buildMap {
        if((grade.isNotBlank() || publish) && gradeTenths(grade)==null) put("grade","Escribe una nota entre 1,0 y 7,0, con hasta un decimal")
        if(publish && feedback.isBlank()) put("feedback","Escribe un comentario para el estudiante")
        else if(feedback.trim().length>2000) put("feedback","Usa hasta 2000 caracteres")
    }
}
