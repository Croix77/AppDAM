package cl.duoc.m.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Identidad visual de cada asignatura. El nombre acompaña siempre al color.
data class SubjectStyle(val ink: Color, val paper: Color, val mark: String, val description: String)

fun subjectStyle(subject: String): SubjectStyle = when(subject) {
    "Lenguaje" -> SubjectStyle(Color(0xFF68479A),Color(0xFFF0E9FA),"Aa","Lee e imagina")
    "Matemática" -> SubjectStyle(Color(0xFF245CAA),Color(0xFFE7EFFD),"2+3","Piensa y resuelve")
    "Ciencias Naturales" -> SubjectStyle(Color(0xFF2F7354),Color(0xFFE5F2E8),"Ho","Observa y descubre")
    else -> SubjectStyle(Color(0xFFA04C30),Color(0xFFFAECE2),"N↑","Ubícate y convive")
}

@Composable
fun SubjectMark(subject: String,small: Boolean=false) {
    val style=subjectStyle(subject)
    Surface(color=style.paper,shape=RoundedCornerShape(14.dp)) {
        Box(Modifier.size(if(small) 40.dp else 52.dp),contentAlignment=androidx.compose.ui.Alignment.Center) {
            if(subject=="Ciencias Naturales") Text("❧",fontSize=if(small) 28.sp else 38.sp,color=style.ink)
            else Text(style.mark,fontSize=if(small) 18.sp else 23.sp,fontWeight=FontWeight.Bold,color=style.ink)
        }
    }
}
