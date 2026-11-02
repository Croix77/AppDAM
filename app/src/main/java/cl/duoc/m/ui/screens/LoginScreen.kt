package cl.duoc.m.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.m.R
import cl.duoc.m.ui.components.*
import cl.duoc.m.viewmodel.SchoolUiState

@Composable
fun LoginScreen(state: SchoolUiState,onLogin: (String,String)->Unit) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Page("Tu espacio para aprender", "MPAZ PRO 2.º · Lenguaje, Matemática, Ciencias e Historia") {
        Image(painterResource(R.drawable.logo_mpaz),"Logo de MPAZ PRO",Modifier.fillMaxWidth().height(110.dp))
        Panel("Iniciar sesión") {
            Text("Usa la cuenta de prueba que te entregó tu docente.")
            Field("Usuario",username,{username=it},state.loginErrors["username"],leadingIcon=Icons.Filled.Person)
            OutlinedTextField(password,{password=it},Modifier.fillMaxWidth(),label={Text("Clave")},singleLine=true,
                visualTransformation=if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),isError=state.loginErrors["password"]!=null,
                leadingIcon={Icon(Icons.Filled.Lock,contentDescription=null)},
                supportingText={state.loginErrors["password"]?.let {Text(it)}},
                trailingIcon={Row(verticalAlignment=Alignment.CenterVertically) {
                    if(state.loginErrors["password"]!=null) Icon(Icons.Filled.Warning,contentDescription="Campo con error",tint=MaterialTheme.colorScheme.error)
                    TextButton(onClick={passwordVisible=!passwordVisible},contentPadding=PaddingValues(horizontal=4.dp)) {
                        Text(if(passwordVisible) "Ocultar" else "Ver")
                    }
                }})
            Action(if(state.busy) "Ingresando…" else "Entrar",!state.busy) { onLogin(username,password);password="" }
        }
        Notice("Prototipo educativo con perfiles ficticios. No ingreses datos personales reales.")
        Text("Cuentas de demostración",style=MaterialTheme.typography.titleMedium)
        Text("Estudiante: estudiante / Aprendo123\nDocente: docente / Profe123\nUTP: utp / Utp12345",style=MaterialTheme.typography.bodyMedium)
    }
}
