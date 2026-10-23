package cl.duoc.m.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import cl.duoc.m.R
import androidx.compose.foundation.layout.Row

@Composable
fun HomeScreen() {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido a MPAZ PRO")
        Text(text = "Aplicación de apoyo educativo")
        Text(text = "Segundo Básico")


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {}) {
                Text(text = "Lenguaje")
            }

            Button(onClick = {}) {
                Text(text = "Matemática")
            }
        }

        Button(
            onClick = {
                Toast.makeText(
                    context,
                    "Iniciando MPAZ PRO",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            Text(text = "Comenzar")
        }


        Image(
            painter = painterResource(id = R.drawable.logo_mpaz),
            contentDescription = "Logo de MPAZ PRO",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentScale = ContentScale.Fit
        )
    }
}