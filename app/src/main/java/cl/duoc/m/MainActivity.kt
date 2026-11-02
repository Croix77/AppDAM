package cl.duoc.m

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import cl.duoc.m.navigation.SchoolApp
import cl.duoc.m.ui.theme.MPAZPRO_GrupoTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MPAZPRO_GrupoTheme {
                val size=calculateWindowSizeClass(this)
                SchoolApp(wide=size.widthSizeClass!=WindowWidthSizeClass.Compact)
            }
        }
    }
}
