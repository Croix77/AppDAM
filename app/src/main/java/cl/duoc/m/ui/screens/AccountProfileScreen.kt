package cl.duoc.m.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.duoc.m.model.Role
import cl.duoc.m.ui.components.*
import cl.duoc.m.viewmodel.SchoolUiState

// El perfil usa los datos de la cuenta que inició sesión, no los del estudiante seleccionado por un docente.
@Composable
fun AccountProfileScreen(s: SchoolUiState,onToggleText: ()->Unit,onLogout: ()->Unit,onHome: ()->Unit) {
    val user=s.user ?: return
    val roleName=when(user.role) {
        Role.ESTUDIANTE -> "Estudiante"
        Role.DOCENTE -> "Docente"
        Role.UTP -> "Coordinación UTP"
    }
    Page("Mi perfil","") {
        Surface(color=Color(0xFFEAF0E7),shape=RoundedCornerShape(22.dp)) {
            Row(Modifier.fillMaxWidth().padding(20.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                Avatar(user.photo,user.name,size=76.dp)
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    Text(user.name,style=MaterialTheme.typography.titleLarge)
                    Text(roleName,style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.primary)
                }
            }
        }
        Panel("Mi información") {
            ProfileInfo("Usuario",user.username)
            ProfileInfo("Identificador",(if(user.role==Role.ESTUDIANTE) "EST-" else "CTA-")+user.id.toString().padStart(3,'0'))
            ProfileInfo(when(user.role) {Role.ESTUDIANTE -> "Curso";Role.DOCENTE -> "Cursos a cargo";Role.UTP -> "Área de trabajo"},if(user.role==Role.DOCENTE) s.students.map {it.course}.distinct().sorted().joinToString(" · ").ifBlank {"Sin cursos asignados"} else user.course)
            if(user.role==Role.ESTUDIANTE) ProfileInfo("Docente asignado",s.assignedTeacherName.ifBlank {"Pendiente de asignación"})
            ProfileInfo("Establecimiento","Escuela Escritora Marcela Paz")
        }
        if(user.role==Role.ESTUDIANTE) {
            Panel("Mis asignaturas") {
                val subjects=s.lessons.map {it.subject}.distinct()
                if(subjects.isEmpty()) Text("Aún no tienes asignaturas asignadas.")
                subjects.forEach {subject -> Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    SubjectMark(subject,small=true)
                    Text(subject,Modifier.weight(1f),style=MaterialTheme.typography.bodyLarge)
                } }
            }
            Notice("Para cambiar tu foto o tus datos, habla con tu docente.")
        } else {
            Panel(if(user.role==Role.DOCENTE) "Mi función" else "Mi área pedagógica") {
                Text(if(user.role==Role.DOCENTE) "Asignaciones, fotos e informes del curso." else "Revisión y publicación de contenidos.")
            }
        }
        Panel("Lectura y sesión") {
            OutlinedButton(onClick=onToggleText,enabled=!s.busy,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {Text(if(s.largeText) "Reducir texto" else "Aumentar texto")}
            OutlinedButton(onClick=onLogout,enabled=!s.busy,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {Text("Salir")}
        }
        Action(when(user.role) {Role.ESTUDIANTE -> "Volver a mis asignaturas";Role.DOCENTE -> "Volver a mis cursos";Role.UTP -> "Volver a gestión pedagógica"},!s.busy,onHome)
    }
}

@Composable
private fun ProfileInfo(label: String,value: String) {
    Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(4.dp)) {
        Text(label,style=MaterialTheme.typography.labelLarge,color=Color(0xFF586A70))
        Text(value,style=MaterialTheme.typography.bodyLarge)
    }
}
