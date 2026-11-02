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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import cl.duoc.m.R
import cl.duoc.m.model.ActivityKind
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun LessonScreen(s: SchoolUiState,onPractice: ()->Unit,onTest: ()->Unit) {
    val l=s.lesson ?: return
    val ready by remember(s.practice,s.answers) {derivedStateOf {s.practice.isNotEmpty() && s.answers.count {it.correct}==s.practice.size}}
    val remaining=(s.assignment?.maxAttempts ?: 0)-s.attempts.size
    Page(l.subject,l.title) {
        Text("Tu avance: ${s.progress}%",style=MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(progress={s.progress/100f},modifier=Modifier.fillMaxWidth())
        s.unavailable?.let { Notice(it,true) }
        AudioHelp()
        Image(painterResource(R.drawable.learning_garden),"Ilustración original: un libro abierto y una planta creciendo",Modifier.fillMaxWidth().height(140.dp))
        Panel("Lo que aprenderás") { Text(l.objective) }
        Panel("Lee y descubre") { Text(l.explanation,style=MaterialTheme.typography.bodyLarge) }
        Panel("Un ejemplo para empezar") { Text(l.example,style=MaterialTheme.typography.bodyLarge) }
        if(l.title=="Un plano para nuestra comunidad") CommunityMap()
        Action("1. Practicar",!s.busy && s.unavailable==null && s.practice.isNotEmpty(),onPractice)
        Action("2. Prueba final · $remaining intentos disponibles",!s.busy && s.unavailable==null && ready && remaining>0 && s.test.isNotEmpty(),onTest)
        if(!ready) Text("Completa correctamente la práctica para habilitar la prueba.")
        if(s.attempts.isNotEmpty()) Panel("Tus intentos") { s.attempts.forEach { Text("Intento ${it.number}: ${it.score} de ${it.total} respuestas correctas · ${it.date}") } }
        Text(l.origin,style=MaterialTheme.typography.labelSmall)
    }
}
@Composable
fun PracticeScreen(s: SchoolUiState,onAnswer: (Long,Int)->Unit,onActivity: (Long,String)->Unit) {
    Page("Practica a tu ritmo","Puedes volver a intentarlo. Lee cada explicación.") {
        s.unavailable?.let { Notice(it,true) }
        var showContext by rememberSaveable(s.lesson?.id) {mutableStateOf(false)}
        OutlinedButton(onClick={showContext=!showContext}) {Text(if(showContext) "Ocultar texto y ejemplo" else "Consultar texto y ejemplo")}
        if(showContext) s.lesson?.let {l -> Panel(l.title) {Text(l.explanation);Text(l.example);if(l.title=="Un plano para nuestra comunidad") CommunityMap()} }
        s.practice.forEachIndexed { index,q ->
            val answer=s.answers.firstOrNull { it.questionId==q.id }
            Panel("${index+1}. ${q.prompt}") {
                val enabled=!s.busy && s.unavailable==null
                when(q.kind) {
                    ActivityKind.SELECCION -> q.options.forEachIndexed { choice,text -> OutlinedButton(onClick={onAnswer(q.id,choice)},modifier=Modifier.fillMaxWidth(),enabled=enabled) { Text("${if(answer?.choice==choice) "✓ " else ""}$text") } }
                    ActivityKind.NUMERO -> {
                        var response by rememberSaveable(q.id) {mutableStateOf("")}
                        OutlinedTextField(if(answer?.correct==true) q.options[q.correct] else response,{response=it},Modifier.fillMaxWidth(),label={Text("Tu respuesta numérica")},singleLine=true,enabled=enabled && answer?.correct!=true,
                            keyboardOptions=androidx.compose.foundation.text.KeyboardOptions(keyboardType=androidx.compose.ui.text.input.KeyboardType.Number))
                        Action("Comprobar resultado",enabled && answer?.correct!=true && response.isNotBlank()) {onActivity(q.id,response)}
                    }
                    ActivityKind.ORDEN -> {
                        var response by rememberSaveable(q.id) {mutableStateOf("")}
                        val steps=q.options[q.correct].split(" → ")
                        val selected=if(answer?.correct==true) steps else if(response.isBlank()) emptyList() else response.split(" → ")
                        Text("Toca los tres pasos en orden. Puedes empezar de nuevo.")
                        steps.reversed().forEach {step -> OutlinedButton(onClick={response=(selected+step).joinToString(" → ")},modifier=Modifier.fillMaxWidth(),enabled=enabled && answer?.correct!=true && step !in selected) {Text(step)} }
                        if(selected.isNotEmpty()) Text(selected.mapIndexed {i,step -> "${i+1}. $step"}.joinToString("\n"))
                        OutlinedButton(onClick={response=""},enabled=enabled && answer?.correct!=true) {Text("Empezar de nuevo")}
                        Action("Comprobar orden",enabled && answer?.correct!=true && selected.size==steps.size) {onActivity(q.id,response)}
                    }
                }
                s.activityFeedback[q.id]?.let {Notice(it,true)}
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
        var showContext by rememberSaveable(s.lesson?.id) {mutableStateOf(false)}
        OutlinedButton(onClick={showContext=!showContext}) {Text(if(showContext) "Ocultar material de consulta" else "Consultar lectura o plano")}
        if(showContext) s.lesson?.let {l -> Panel("Material de consulta") {Text(l.explanation);if(l.title=="Un plano para nuestra comunidad") CommunityMap()} }
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

@Composable
private fun CommunityMap() {
    Panel("Plano de la comunidad · Norte ↑") {
        listOf(listOf("Biblioteca","Escuela","Consultorio"),listOf("Panadería","Plaza","Bomberos"),listOf("Cancha","Paradero","Huerto")).forEach {row ->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                row.forEach {place -> Surface(Modifier.weight(1f).heightIn(min=70.dp),color=if(place=="Plaza") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,shape=MaterialTheme.shapes.small) {
                    Box(Modifier.padding(6.dp),contentAlignment=androidx.compose.ui.Alignment.Center) {Text(place,style=MaterialTheme.typography.bodyMedium,textAlign=androidx.compose.ui.text.style.TextAlign.Center)}
                } }
            }
        }
        Text("Oeste ←    → Este\nSur ↓",style=MaterialTheme.typography.labelLarge)
    }
}
