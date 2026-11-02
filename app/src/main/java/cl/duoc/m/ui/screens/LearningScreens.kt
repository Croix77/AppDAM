package cl.duoc.m.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.m.ui.components.*
import cl.duoc.m.viewmodel.SchoolUiState

@Composable
fun LessonScreen(s: SchoolUiState,onPractice: ()->Unit,onTest: ()->Unit) {
    val l=s.lesson ?: return
    val ready=s.practice.isNotEmpty() && s.answers.count { it.correct }==s.practice.size
    val remaining=(s.assignment?.maxAttempts ?: 0)-s.attempts.size
    Page(l.subject,l.title) {
        Text("Tu avance: ${s.progress}%",style=MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(progress=s.progress/100f,modifier=Modifier.fillMaxWidth())
        s.unavailable?.let { Notice(it,true) }
        Panel("Lo que aprenderás") { Text(l.objective) }
        Panel("Lee y descubre") { Text(l.explanation,style=MaterialTheme.typography.bodyLarge) }
        Panel("Un ejemplo para empezar") { Text(l.example,style=MaterialTheme.typography.bodyLarge) }
        Action("1. Practicar",s.unavailable==null && s.practice.isNotEmpty(),onPractice)
        Action("2. Prueba final · $remaining intentos disponibles",s.unavailable==null && ready && remaining>0 && s.test.isNotEmpty(),onTest)
        if(!ready) Text("Completa correctamente la práctica para habilitar la prueba.")
        if(s.attempts.isNotEmpty()) Panel("Tus intentos") { s.attempts.forEach { Text("Intento ${it.number}: ${it.score} de ${it.total} respuestas correctas · ${it.date}") } }
        Text(l.origin,style=MaterialTheme.typography.labelSmall)
    }
}
@Composable
fun PracticeScreen(s: SchoolUiState,onAnswer: (Long,Int)->Unit) {
    Page("Practica a tu ritmo","Puedes volver a intentarlo. Lee cada explicación.") {
        s.unavailable?.let { Notice(it,true) }
        s.practice.forEachIndexed { index,q ->
            val answer=s.answers.firstOrNull { it.questionId==q.id }
            Panel("${index+1}. ${q.prompt}") {
                q.options.forEachIndexed { choice,text -> OutlinedButton(onClick={onAnswer(q.id,choice)},modifier=Modifier.fillMaxWidth(),enabled=!s.busy && s.unavailable==null) { Text("${if(answer?.choice==choice) "✓ " else ""}$text") } }
                AnimatedVisibility(answer!=null) {
                    val color by animateColorAsState(if(answer?.correct==true) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer,label="Respuesta")
                    Surface(color=color,shape=MaterialTheme.shapes.medium) { Text("${if(answer?.correct==true) "¡Bien hecho!" else "Probemos otra vez."} ${q.explanation}",Modifier.fillMaxWidth().padding(16.dp)) }
                }
            }
        }
    }
}
@Composable
fun TestScreen(s: SchoolUiState,onChoose: (Long,Int)->Unit,onSubmit: ()->Unit) {
    val remaining=(s.assignment?.maxAttempts ?: 0)-s.attempts.size
    Page("Comprueba lo aprendido","Cada envío completo usa un intento. Puedes salir sin enviar.") {
        s.unavailable?.let { Notice(it,true) }
        Text("Intentos disponibles: $remaining")
        s.test.forEachIndexed { index,q -> Panel("${index+1}. ${q.prompt}") {
            q.options.forEachIndexed { choice,text -> OutlinedButton(onClick={onChoose(q.id,choice)},modifier=Modifier.fillMaxWidth(),enabled=!s.busy && remaining>0 && s.unavailable==null) {Text("${if(s.choices[q.id]==choice) "● " else "○ "}$text")} }
        } }
        Action(if(s.busy) "Guardando…" else "Enviar prueba",!s.busy && s.unavailable==null && remaining>0 && s.test.isNotEmpty() && s.choices.size==s.test.size,onSubmit)
        if(s.review.isNotEmpty()) Panel("Revisión del último intento") {
            s.review.forEach { (q,a) ->
                Text(q.prompt,style=MaterialTheme.typography.titleMedium)
                Text("Tu respuesta: ${q.options[a.choice]}")
                Notice("${if(a.correct) "Correcta." else "Por reforzar."} ${q.explanation}",!a.correct)
            }
        }
    }
}
