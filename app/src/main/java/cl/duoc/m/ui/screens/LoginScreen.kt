package cl.duoc.m.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import cl.duoc.m.ui.components.*
import cl.duoc.m.viewmodel.SchoolUiState

@Composable
fun LoginScreen(state: SchoolUiState,onLogin: (String,String)->Unit) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Page("Aprende a tu ritmo", "Segundo Básico · Lenguaje, Matemática, Ciencias e Historia") {
        BrandLockup()
        Panel("Iniciar sesión") {
            Text("Usa la cuenta de prueba que te entregó tu docente.")
            Field("Usuario",username,{username=it},state.loginErrors["username"])
            OutlinedTextField(password,{password=it},Modifier.fillMaxWidth(),label={Text("Clave")},singleLine=true,visualTransformation=PasswordVisualTransformation(),isError=state.loginErrors["password"]!=null,
                supportingText={state.loginErrors["password"]?.let {Text(it)}},trailingIcon={if(state.loginErrors["password"]!=null) Text("!")})
            Action(if(state.busy) "Ingresando…" else "Entrar",!state.busy) { onLogin(username,password);password="" }
        }
        Notice("Prototipo educativo con perfiles ficticios. No ingreses datos personales reales.")
        Text("Cuentas de demostración",style=MaterialTheme.typography.titleMedium)
        Text("Estudiante: estudiante / Aprendo123\nDocente: docente / Profe123\nUTP: utp / Utp12345",style=MaterialTheme.typography.bodyMedium)
    }
}
