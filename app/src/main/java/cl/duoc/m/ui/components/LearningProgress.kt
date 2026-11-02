package cl.duoc.m.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LearningProgress(label: String, position: Int, total: Int, completed: Int, completedLabel: String) {
    Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Text("$label $position de $total",style=MaterialTheme.typography.titleLarge)
        Text("$completedLabel: $completed/$total",style=MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(progress={if(total==0) 0f else completed.toFloat()/total},modifier=Modifier.fillMaxWidth())
    }
}

@Composable
fun PracticeCompleted(total: Int, onBack: ()->Unit, onReview: ()->Unit) {
    Page("¡Práctica terminada!","Completaste todas las actividades de esta unidad.") {
        Panel("✓ $total/$total actividades completadas") {
            Text("Tu avance ya quedó guardado en este dispositivo.")
            Text("Ahora puedes volver a la unidad para rendir la prueba o revisar lo aprendido.")
        }
        Action("Volver a la unidad",onClick=onBack)
        OutlinedButton(onClick=onReview,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {Text("Revisar actividades")}
    }
}
