package cl.duoc.m.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.m.R

@Composable
fun BrandLockup(modifier: Modifier=Modifier) {
    Row(modifier,verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
        Image(painterResource(R.drawable.explora_logo),"Libro y estrella de Explora Marcela Paz",Modifier.width(72.dp).height(52.dp))
        Column {
            Text("Explora",fontSize=24.sp,lineHeight=27.sp,fontWeight=FontWeight.Bold,color=Color(0xFF0C3440))
            Text("Marcela Paz",fontSize=18.sp,lineHeight=22.sp,color=Color(0xFF0C3440))
        }
    }
}
