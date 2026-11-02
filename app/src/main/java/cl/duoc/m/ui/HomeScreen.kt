package cl.duoc.m.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.m.R
import cl.duoc.m.model.*
import cl.duoc.m.ui.components.*
import cl.duoc.m.ui.screens.Avatar

// Conserva bienvenida, logo y mensaje de Comenzar; agrega las unidades asignadas.
@Composable
fun HomeScreen(user: User,lessons: List<Lesson>,wide: Boolean,onLesson: (Long)->Unit,onBegin: ()->Unit) {
    val context=LocalContext.current
    Page("Bienvenido a MPAZ PRO", "Hola, ${user.name}. Aprende a tu ritmo.") {
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("Aplicación de apoyo educativo",style=MaterialTheme.typography.titleLarge)
                Text("Segundo Básico · ${user.course}")
            }
            Image(painterResource(R.drawable.logo_mpaz),"Logo de MPAZ PRO",Modifier.size(84.dp))
        }
        Avatar(user.photo,user.name)
        Action("Comenzar",lessons.isNotEmpty()) { Toast.makeText(context,"Iniciando MPAZ PRO",Toast.LENGTH_SHORT).show();onBegin() }
        Text("Elige una asignatura",style=MaterialTheme.typography.titleLarge)
        if(lessons.isEmpty()) Notice("Tu docente aún no te asignó unidades publicadas.")
        lessons.chunked(if(wide) 2 else 1).forEach { pair -> Row(horizontalArrangement=Arrangement.spacedBy(16.dp)) {
            pair.forEach { lesson -> Card(onClick={onLesson(lesson.id)},modifier=Modifier.weight(1f),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Text(lesson.subject,style=MaterialTheme.typography.labelLarge)
                    Text(lesson.title,style=MaterialTheme.typography.titleLarge)
                    Text("Leer, practicar y comprobar lo aprendido")
                    Text("Abrir unidad →",color=MaterialTheme.colorScheme.primary)
                }
            } }
            if(wide && pair.size==1) Spacer(Modifier.weight(1f))
        } }
        Notice("Tu avance se guarda en este dispositivo. Si necesitas ayuda, consulta a tu docente.")
    }
}
