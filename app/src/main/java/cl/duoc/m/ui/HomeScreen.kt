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

// Conserva bienvenida, logo y mensaje de Comenzar; agrupa las unidades por asignatura.
@Composable
fun HomeScreen(user: User,lessons: List<Lesson>,columns: Int,onSubject: (String)->Unit,onBegin: ()->Unit) {
    val context=LocalContext.current
    val subjects=lessons.map { it.subject }.distinct()
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
        if(subjects.isEmpty()) Notice("Tu docente aún no te asignó unidades publicadas.")
        subjects.chunked(columns).forEach { pair -> Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(16.dp)) {
            pair.forEach { subject -> Card(onClick={onSubject(subject)},modifier=Modifier.weight(1f),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Text(subject,style=MaterialTheme.typography.titleLarge)
                    Text("${lessons.count { it.subject==subject }} unidad(es) asignada(s)")
                    Text("Ver unidades →",color=MaterialTheme.colorScheme.primary)
                }
            } }
            repeat(columns-pair.size) {Spacer(Modifier.weight(1f))}
        } }
        Notice("Tu avance se guarda en este dispositivo. Si necesitas ayuda, consulta a tu docente.")
    }
}
