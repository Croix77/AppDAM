package cl.duoc.m.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.m.R
import cl.duoc.m.model.*
import cl.duoc.m.ui.components.*
import cl.duoc.m.ui.screens.Avatar

@Composable
fun HomeScreen(
    user: User, lessons: List<Lesson>, columns: Int,
    onLesson: (Long)->Unit, onBegin: ()->Unit,
    progress: Map<Long,Int> = emptyMap(), busy: Boolean = false
) {
    val context=LocalContext.current
    var subject by rememberSaveable(user.id) {mutableStateOf<String?>(null)}
    val next=lessons.firstOrNull { (progress[it.id] ?: 0) in 1..99 }
        ?: lessons.firstOrNull { (progress[it.id] ?: 0)==0 } ?: lessons.firstOrNull()
    val groups=lessons.groupBy {it.subject}.entries.toList()
    val completed=lessons.count {(progress[it.id] ?: 0)==100}
    Box(Modifier.fillMaxSize().background(Color(0xFFF7F6F2)),contentAlignment=Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max=1120.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement=Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                Avatar(user.photo,user.name,size=44.dp)
                Column(Modifier.weight(1f)) {
                    Text("Bienvenido a MPAZ PRO",fontSize=12.sp,color=Color(0xFF65716F))
                    Text("Hola, ${user.name.removePrefix("Estudiante ")}",style=MaterialTheme.typography.titleLarge)
                }
                Surface(shape=RoundedCornerShape(8.dp),color=Color.White) {Text("2.º BÁSICO",Modifier.padding(8.dp),fontSize=11.sp,fontWeight=FontWeight.Bold,color=Color(0xFF52605F))}
            }

            // Una sola acción principal; el avance procede de SQLite, no de cifras decorativas.
            Surface(color=Color(0xFF203D43),shape=RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Text("TU CUADERNO DE EXPLORACIÓN",Modifier.weight(1f),fontSize=11.sp,letterSpacing=1.sp,color=Color(0xFFB7DBCE),fontWeight=FontWeight.Bold)
                        Surface(color=Color(0xFFF7F6F2),shape=RoundedCornerShape(10.dp)) {Image(painterResource(R.drawable.logo_mpaz),"Logo de MPAZ PRO",Modifier.size(40.dp).padding(4.dp))}
                    }
                    Text(if(next!=null && (progress[next.id] ?: 0)>0) "Un paso más, a tu ritmo." else "Hoy toca descubrir.",fontSize=24.sp,lineHeight=30.sp,fontWeight=FontWeight.Bold,color=Color.White)
                    if(next!=null) {
                        Text("${next.subject} · ${next.title}",fontSize=15.sp,lineHeight=21.sp,color=Color(0xFFDCDFDA))
                        val value=progress[next.id] ?: 0
                        if(value>0) {
                            LinearProgressIndicator(progress={value/100f},modifier=Modifier.fillMaxWidth(),color=Color(0xFFBCE7CC),trackColor=Color(0xFF4C666A))
                            Text("$value% del recorrido completado",fontSize=12.sp,color=Color(0xFFDCDFDA))
                        }
                        Button(onClick={
                            if(value==0) Toast.makeText(context,"Iniciando MPAZ PRO",Toast.LENGTH_SHORT).show()
                            if(next==lessons.firstOrNull() && value==0) onBegin() else onLesson(next.id)
                        },enabled=!busy,colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFD5EDD5),contentColor=Color(0xFF173C35)),shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth().heightIn(min=50.dp)) {
                            Text(if(value>0) "Continuar aprendiendo →" else "Comenzar",fontWeight=FontWeight.Bold)
                        }
                    } else Text("Tu docente aún no te asignó unidades publicadas.",color=Color.White)
                }
            }

            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Text("¿Qué exploramos?",Modifier.weight(1f),style=MaterialTheme.typography.titleLarge)
                Text("$completed/${lessons.size} unidades",fontSize=12.sp,color=Color(0xFF65716F))
            }
            val tileColumns=if(columns>=3) 4 else 2
            groups.chunked(tileColumns).forEach {row ->
                Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    row.forEach {group ->
                        val style=subjectStyle(group.key)
                        Card(onClick={subject=if(subject==group.key) null else group.key},enabled=!busy,modifier=Modifier.weight(1f),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=if(subject==group.key) style.paper else Color.White)) {
                            Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                    SubjectMark(group.key,small=true)
                                    Text(group.key,Modifier.weight(1f),style=MaterialTheme.typography.titleMedium.copy(fontSize=15.sp,lineHeight=19.sp),color=style.ink)
                                }
                                Text(style.description,fontSize=12.sp,lineHeight=17.sp,color=Color(0xFF59645F))
                                Text("${group.value.size} unidades ${if(subject==group.key) "· seleccionada" else "→"}",fontSize=11.sp,color=style.ink)
                            }
                        }
                    }
                    repeat(tileColumns-row.size) {Spacer(Modifier.weight(1f))}
                }
            }

            Row(verticalAlignment=Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(subject ?: "Tus unidades",style=MaterialTheme.typography.titleLarge)
                    Text("Elige un recorrido para empezar",fontSize=13.sp,color=Color(0xFF65716F))
                }
                if(subject!=null) TextButton(onClick={subject=null}) {Text("Ver todas")}
            }
            lessons.filter {subject==null || it.subject==subject}.forEach {lesson ->
                val value=progress[lesson.id] ?: 0
                val style=subjectStyle(lesson.subject)
                Card(onClick={onLesson(lesson.id)},enabled=!busy,shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        SubjectMark(lesson.subject,small=true)
                        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                            Text(lesson.title,fontSize=16.sp,lineHeight=21.sp,fontWeight=FontWeight.SemiBold)
                            Text(when {value==100 -> "✓ Completada";value>0 -> "En curso · $value%";else -> "Por comenzar"},fontSize=12.sp,color=style.ink)
                        }
                        Text("→",fontSize=22.sp,color=style.ink)
                    }
                }
            }
            Text("APRENDE · PRACTICA · DESCUBRE\nTu avance se guarda en este dispositivo.",fontSize=12.sp,lineHeight=19.sp,color=Color(0xFF65716F))
            Spacer(Modifier.height(12.dp))
        }
    }
}
