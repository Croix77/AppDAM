package cl.duoc.m.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.m.ui.components.SubjectMark
import cl.duoc.m.viewmodel.SchoolUiState

// Muestra un solo ejercicio. Las respuestas y el cálculo de avance siguen en el ViewModel.
@Composable
fun MathPracticeScreen(s: SchoolUiState,onActivity: (Long,String)->Unit,onBackToLesson: ()->Unit) {
    if(s.practice.isEmpty()) return
    var index by rememberSaveable(s.lesson?.id) {mutableStateOf(s.practice.indexOfFirst {q -> s.answers.none {it.questionId==q.id && it.correct}}.coerceAtLeast(0))}
    index=index.coerceIn(0,s.practice.lastIndex)
    val q=s.practice[index]
    val correct=s.answers.any {it.questionId==q.id && it.correct}
    var response by rememberSaveable(q.id) {mutableStateOf("")}
    var showHelp by rememberSaveable(q.id) {mutableStateOf(false)}
    val enabled=!s.busy && s.unavailable==null
    val focus=LocalFocusManager.current
    val blue=Color(0xFF245CAA)
    val completed=s.practice.count {task -> s.answers.any {it.questionId==task.id && it.correct}}
    val scroll=rememberScrollState()
    LaunchedEffect(q.id) {scroll.scrollTo(0)}
    Box(Modifier.fillMaxSize().background(Color(0xFFF3F6FC)),contentAlignment=Alignment.TopCenter) {
        Column(Modifier.widthIn(max=760.dp).fillMaxWidth().verticalScroll(scroll).padding(20.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                SubjectMark("Matemática",small=true)
                Column(Modifier.weight(1f)) {
                    Text("MATEMÁTICA · PRÁCTICA",fontSize=11.sp,letterSpacing=1.sp,color=blue,fontWeight=FontWeight.Bold)
                    Text("Resolvemos en la feria",style=MaterialTheme.typography.titleLarge)
                }
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Text("Reto ${index+1} de ${s.practice.size}",fontSize=14.sp,fontWeight=FontWeight.Bold,color=blue)
                Text("$completed completados",fontSize=13.sp,color=Color(0xFF637185))
            }
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                s.practice.forEachIndexed {i,task ->
                    val done=s.answers.any {it.questionId==task.id && it.correct}
                    Surface(Modifier.weight(1f).height(6.dp),shape=RoundedCornerShape(6.dp),color=if(done) Color(0xFF397D65) else if(i==index) blue else Color(0xFFD7E0EE)) {}
                }
            }
            s.unavailable?.let {Text(it,color=MaterialTheme.colorScheme.error)}

            Surface(color=Color.White,shape=RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(22.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
                    Text(listOf("CONSTRUYE EL NÚMERO","REÚNE CANTIDADES","ENCUENTRA LO QUE QUEDA","DESCUBRE EL PATRÓN").getOrElse(index) {"RESUELVE EL RETO"},fontSize=11.sp,letterSpacing=1.sp,color=blue,fontWeight=FontWeight.Bold)
                    NumberClues(q.prompt,index)
                    Text(q.prompt,fontSize=21.sp,lineHeight=29.sp,fontWeight=FontWeight.SemiBold,color=Color(0xFF21354C))
                    Text("Piensa, escribe y comprueba. Puedes corregir tu respuesta.",fontSize=14.sp,lineHeight=20.sp,color=Color(0xFF637185))
                    OutlinedTextField(
                        value=if(correct) q.options[q.correct] else response,
                        onValueChange={response=it},modifier=Modifier.fillMaxWidth(),
                        label={Text("Tu respuesta numérica")},singleLine=true,readOnly=correct,enabled=enabled,
                        textStyle=MaterialTheme.typography.headlineMedium.copy(fontSize=32.sp,textAlign=TextAlign.Center,color=Color(0xFF21354C)),
                        keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),shape=RoundedCornerShape(14.dp),
                        colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=blue,focusedLabelColor=blue)
                    )
                    if(!correct) Button(onClick={focus.clearFocus();onActivity(q.id,response)},enabled=enabled && response.isNotBlank(),modifier=Modifier.fillMaxWidth().heightIn(min=54.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.buttonColors(containerColor=blue)) {
                        Text(if(s.busy) "Comprobando…" else "Comprobar resultado",fontWeight=FontWeight.Bold)
                    }
                    s.activityFeedback[q.id]?.let {feedback -> Surface(color=Color(0xFFFFF1DE),shape=RoundedCornerShape(12.dp)) {Text(feedback,Modifier.padding(16.dp),color=Color(0xFF75470B),fontSize=16.sp,lineHeight=23.sp)} }
                    AnimatedVisibility(correct) {
                        Surface(color=Color(0xFFE6F3EB),shape=RoundedCornerShape(14.dp)) {
                            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                                Text("✓ ¡Bien hecho!",fontWeight=FontWeight.Bold,fontSize=19.sp,color=Color(0xFF24583F))
                                Text(q.explanation,fontSize=16.sp,lineHeight=23.sp,color=Color(0xFF24583F))
                                Text("Este reto ya quedó guardado.",fontSize=12.sp,color=Color(0xFF436650))
                            }
                        }
                    }
                }
            }
            Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick={focus.clearFocus();index--},enabled=enabled && index>0,modifier=Modifier.weight(1f).heightIn(min=50.dp),shape=RoundedCornerShape(12.dp)) {Text("← Anterior")}
                Button(onClick={focus.clearFocus();if(index<s.practice.lastIndex) index++ else onBackToLesson()},enabled=enabled && correct,modifier=Modifier.weight(1f).heightIn(min=50.dp),shape=RoundedCornerShape(12.dp),colors=ButtonDefaults.buttonColors(containerColor=blue)) {Text(if(index<s.practice.lastIndex) "Siguiente reto →" else "Ver mi avance →")}
            }
            TextButton(onClick={showHelp=!showHelp},modifier=Modifier.align(Alignment.CenterHorizontally)) {Text(if(showHelp) "Cerrar ejemplo" else "¿Necesitas una pista? Ver ejemplo",color=blue)}
            if(showHelp) Surface(color=Color(0xFFE4ECFA),shape=RoundedCornerShape(16.dp)) {Text(s.lesson?.example ?: "",Modifier.padding(18.dp),fontSize=16.sp,lineHeight=24.sp,color=Color(0xFF21354C))}
            Text("Sin apuro: cada intento de práctica te ayuda a aprender.",fontSize=12.sp,lineHeight=18.sp,color=Color(0xFF637185))
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun NumberClues(prompt: String,index: Int) {
    val numbers=Regex("\\d+").findAll(prompt).map {it.value}.take(5).toList()
    val tokens=when(index) {
        0 -> numbers.take(2).mapIndexed {i,n -> "$n ${if(i==0) "D" else "U"}"}
        1 -> listOfNotNull(numbers.getOrNull(0),"+",numbers.getOrNull(1))
        2 -> listOfNotNull(numbers.getOrNull(0),"−",numbers.getOrNull(1))
        else -> numbers.dropLast(1)+"?"+numbers.takeLast(1)
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        tokens.forEach {token -> Surface(Modifier.weight(1f).heightIn(min=60.dp),shape=RoundedCornerShape(12.dp),color=if(token=="?") Color(0xFFFFE8AD) else Color(0xFFEAF0FC)) {
            Box(Modifier.padding(vertical=12.dp,horizontal=3.dp),contentAlignment=Alignment.Center) {Text(token,fontSize=if(tokens.size>4) 20.sp else 28.sp,fontWeight=FontWeight.Bold,color=Color(0xFF245CAA))}
        } }
    }
}
