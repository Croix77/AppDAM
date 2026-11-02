package cl.duoc.m.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.m.model.Lesson
import cl.duoc.m.model.User
import cl.duoc.m.ui.components.*

@Composable
fun SubjectScreen(user: User,subject: String,lessons: List<Lesson>,columns: Int,onLesson: (Long)->Unit) {
    Page(subject, "${user.name} · ${user.course}") {
        Text("Elige una unidad",style=MaterialTheme.typography.titleLarge)
        if(lessons.isEmpty()) Notice("No tienes unidades publicadas y asignadas en esta asignatura.")
        lessons.chunked(columns).forEach { pair -> Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(16.dp)) {
            pair.forEach { lesson -> Card(onClick={onLesson(lesson.id)},modifier=Modifier.weight(1f),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Text("Unidad",style=MaterialTheme.typography.labelLarge)
                    Text(lesson.title,style=MaterialTheme.typography.titleLarge)
                    Text(lesson.objective)
                    Text("Abrir unidad →",color=MaterialTheme.colorScheme.primary)
                }
            } }
            repeat(columns-pair.size) {Spacer(Modifier.weight(1f))}
        } }
    }
}
