package cl.duoc.m.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.m.R

data class SubjectStyle(val ink: Color, val paper: Color, val icon: Int)

fun subjectStyle(subject: String): SubjectStyle = when(subject) {
    "Lenguaje" -> SubjectStyle(Color(0xFF8572B8),Color(0xFFF0EAF5),R.drawable.subject_language)
    "Matemática" -> SubjectStyle(Color(0xFF418BC0),Color(0xFFE6F0F7),R.drawable.subject_math)
    "Ciencias Naturales" -> SubjectStyle(Color(0xFF709C65),Color(0xFFEAF2DE),R.drawable.subject_science)
    else -> SubjectStyle(Color(0xFFD97957),Color(0xFFFAEAE1),R.drawable.subject_history)
}

@Composable
fun SubjectMark(subject: String,small: Boolean=false) {
    val style=subjectStyle(subject)
    Surface(color=style.paper,shape=RoundedCornerShape(12.dp)) {
        Image(painterResource(style.icon),null,Modifier.size(if(small) 40.dp else 52.dp).padding(7.dp))
    }
}
