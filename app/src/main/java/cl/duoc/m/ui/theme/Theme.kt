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
        primary=Color(0xFF126B62),onPrimary=Color.White,primaryContainer=Color(0xFFC4EFE3),
        secondary=Color(0xFF465D78),secondaryContainer=Color(0xFFE2EDF4),onSecondaryContainer=Color(0xFF18374B),
        background=Color(0xFFF4F7F5),surface=Color.White,onBackground=Color(0xFF1D3336),onSurface=Color(0xFF1D3336),
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
