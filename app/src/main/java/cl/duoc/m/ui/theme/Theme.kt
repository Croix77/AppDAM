package cl.duoc.m.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun MPAZPRO_GrupoTheme(
    largeText: Boolean = false,
    content: @Composable () -> Unit
) {
    val size=if(largeText) 21 else 18
    MaterialTheme(colorScheme=lightColorScheme(
        primary=Color(0xFF124F54),onPrimary=Color.White,primaryContainer=Color(0xFFDCEFC5),
        secondary=Color(0xFF7D6AA7),secondaryContainer=Color(0xFFE6E0EF),onSecondaryContainer=Color(0xFF18374B),
        background=Color(0xFFFCFAF5),surface=Color(0xFFFFFEFB),onBackground=Color(0xFF0C3440),onSurface=Color(0xFF0C3440),
        onSurfaceVariant=Color(0xFF4C6062),outline=Color(0xFF708784)
    ),typography=Typography(
        headlineMedium=TextStyle(fontSize=28.sp,lineHeight=34.sp,fontWeight=FontWeight.Bold),
        titleLarge=TextStyle(fontSize=22.sp,lineHeight=28.sp,fontWeight=FontWeight.SemiBold),
        titleMedium=TextStyle(fontSize=19.sp,lineHeight=26.sp,fontWeight=FontWeight.SemiBold),
        bodyLarge=TextStyle(fontSize=size.sp,lineHeight=(size+9).sp),
        bodyMedium=TextStyle(fontSize=(size-1).sp,lineHeight=(size+7).sp),
        labelLarge=TextStyle(fontSize=15.sp,lineHeight=22.sp,fontWeight=FontWeight.Medium)
    )) {
        content()
    }
}
