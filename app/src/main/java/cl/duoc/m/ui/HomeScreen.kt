package cl.duoc.m.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.m.R
import cl.duoc.m.model.*
import cl.duoc.m.ui.components.*
import cl.duoc.m.ui.screens.Avatar

@Composable
fun HomeScreen(
    user: User, lessons: List<Lesson>, columns: Int,
    onLesson: (Long)->Unit, onSubject: (String)->Unit,
    progress: Map<Long,Int> = emptyMap(), busy: Boolean = false,
    onLogout: ()->Unit={}, onToggleText: ()->Unit={}, largeText: Boolean=false
) {
    val context=LocalContext.current
    var showSettings by rememberSaveable {mutableStateOf(false)}
    val next=lessons.firstOrNull {(progress[it.id] ?: 0) in 1..99}
        ?: lessons.firstOrNull {it.title=="La biblioteca viajera" && (progress[it.id] ?: 0)==0}
        ?: lessons.firstOrNull {(progress[it.id] ?: 0)==0} ?: lessons.firstOrNull()
    val groups=lessons.groupBy {it.subject}.entries.toList()
    val ink=Color(0xFF0C3440)
    Box(Modifier.fillMaxSize().background(Color(0xFFFCFAF5)),contentAlignment=Alignment.TopCenter) {
        Column(Modifier.widthIn(max=1000.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal=20.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                BrandLockup(Modifier.weight(1f))
                Surface(onClick={showSettings=!showSettings},enabled=!busy,shape=CircleShape,color=Color(0xFFE6E0EF),modifier=Modifier.semantics {contentDescription="Opciones del estudiante"}) {
                    Avatar(user.photo,user.name,size=44.dp)
                }
            }
            if(showSettings) Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick=onToggleText,enabled=!busy) {Text(if(largeText) "Reducir texto" else "Aumentar texto")}
                OutlinedButton(onClick=onLogout,enabled=!busy) {Text("Salir")}
            }
            Text("Hola, ${user.name.removePrefix("Estudiante ")}",fontSize=24.sp,lineHeight=30.sp,fontWeight=FontWeight.Bold,color=ink)

            // La ilustración es una imagen; todo el texto y el botón son Compose real.
            Surface(color=Color(0xFF124F54),shape=RoundedCornerShape(18.dp)) {
                Box(Modifier.fillMaxWidth().heightIn(min=184.dp)) {
                    Box(Modifier.matchParentSize()) {
                        Image(painterResource(R.drawable.explora_hero),null,Modifier.align(Alignment.BottomEnd).width(300.dp).fillMaxHeight(),contentScale=ContentScale.Fit,alignment=Alignment.BottomEnd)
                    }
                    Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                        Text("Tu próximo\ndescubrimiento",fontSize=25.sp,lineHeight=29.sp,fontWeight=FontWeight.Bold,color=Color.White)
                        if(next!=null) {
                            Text("${next.subject} · ${next.title}",fontSize=13.sp,lineHeight=18.sp,color=Color(0xFFF2F5E9))
                            Button(onClick={Toast.makeText(context,"Aprendamos con Explora Marcela Paz",Toast.LENGTH_SHORT).show();onLesson(next.id)},enabled=!busy,
                                colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFB7D785),contentColor=ink),shape=RoundedCornerShape(24.dp),contentPadding=PaddingValues(horizontal=16.dp,vertical=10.dp),modifier=Modifier.heightIn(min=46.dp)) {
                                Text("Continuar aprendiendo",fontSize=14.sp,fontWeight=FontWeight.SemiBold)
                                Spacer(Modifier.width(10.dp))
                                Image(painterResource(R.drawable.explora_chevron),null,Modifier.size(18.dp))
                            }
                        } else Text("Tu docente pronto te asignará una unidad.",fontSize=14.sp,color=Color.White)
                    }
                }
            }

            Text("¿Qué exploramos?",fontSize=23.sp,lineHeight=28.sp,fontWeight=FontWeight.Bold,color=ink)
            val tileColumns=if(columns>=3) 4 else 2
            groups.chunked(tileColumns).forEach {row ->
                Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    row.forEach {group ->
                        val style=subjectStyle(group.key)
                        Card(onClick={onSubject(group.key)},enabled=!busy,modifier=Modifier.weight(1f),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=style.paper)) {
                            Box(Modifier.fillMaxWidth().height(if(largeText) 174.dp else 140.dp)) {
                                Image(painterResource(R.drawable.explora_wave),null,Modifier.matchParentSize(),contentScale=ContentScale.FillBounds,colorFilter=ColorFilter.tint(style.ink.copy(alpha=.15f)))
                                Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                                    Image(painterResource(style.icon),null,Modifier.size(42.dp))
                                    Text(group.key,fontSize=if(largeText) 18.sp else 15.sp,lineHeight=20.sp,fontWeight=FontWeight.Medium,color=ink)
                                }
                                Surface(Modifier.align(Alignment.BottomEnd).padding(12.dp).size(25.dp),shape=CircleShape,color=Color.White.copy(alpha=.88f)) {
                                    Image(painterResource(R.drawable.explora_chevron),null,Modifier.padding(4.dp))
                                }
                            }
                        }
                    }
                    repeat(tileColumns-row.size) {Spacer(Modifier.weight(1f))}
                }
            }
            HorizontalDivider(color=Color(0xFFDDDCD3))
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                Image(painterResource(R.drawable.explora_saved),null,Modifier.size(23.dp))
                Text("Tu avance se guarda automáticamente",fontSize=12.sp,lineHeight=18.sp,color=Color(0xFF496773))
            }
        }
    }
}

@Composable
fun SubjectUnitsScreen(subject: String,lessons: List<Lesson>,progress: Map<Long,Int>,busy: Boolean,onLesson: (Long)->Unit) {
    Page(subject,"Elige una unidad y continúa aprendiendo a tu ritmo.") {
        lessons.filter {it.subject==subject}.forEach {lesson ->
            val value=progress[lesson.id] ?: 0
            Card(onClick={onLesson(lesson.id)},enabled=!busy,colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    SubjectMark(subject,small=true)
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                        Text(lesson.title,style=MaterialTheme.typography.titleMedium)
                        Text(when {value==100 -> "✓ Completada";value>0 -> "En curso · $value%";else -> "Por comenzar"},fontSize=13.sp,color=Color(0xFF496773))
                    }
                    Image(painterResource(R.drawable.explora_chevron),null,Modifier.size(22.dp))
                }
            }
        }
    }
}
