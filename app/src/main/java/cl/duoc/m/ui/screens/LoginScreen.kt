package cl.duoc.m.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import cl.duoc.m.R
import cl.duoc.m.ui.components.BrandLockup
import cl.duoc.m.viewmodel.SchoolUiState

@Composable
fun LoginScreen(state: SchoolUiState,onLogin: (String,String)->Unit,onToggleText: ()->Unit={}) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var showAccounts by rememberSaveable { mutableStateOf(false) }
    val focus=LocalFocusManager.current
    val submit={
        if(!state.busy) {focus.clearFocus();onLogin(username,password);password="";visible=false}
    }
    BoxWithConstraints(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val wide=maxWidth>=840.dp
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(horizontal=24.dp,vertical=20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(20.dp)) {
            Row(Modifier.widthIn(max=1000.dp).fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
                BrandLockup()
                TextButton(onClick=onToggleText,enabled=!state.busy) {Text(if(state.largeText) "A−" else "A+")}
            }
            if(wide) {
                Row(Modifier.widthIn(max=1000.dp).fillMaxWidth().padding(vertical=24.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(40.dp)) {
                    LoginWelcome(Modifier.weight(1f),true)
                    LoginForm(state,username,{username=it},password,{password=it},visible,{visible=!visible},submit,Modifier.weight(1f))
                }
            } else {
                LoginWelcome(Modifier.widthIn(max=460.dp).fillMaxWidth(),false)
                LoginForm(state,username,{username=it},password,{password=it},visible,{visible=!visible},submit,Modifier.widthIn(max=460.dp).fillMaxWidth())
            }
            Column(Modifier.widthIn(max=460.dp).fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally) {
                TextButton(onClick={showAccounts=!showAccounts}) {Text(if(showAccounts) "Ocultar accesos de prueba" else "Accesos de prueba")}
                if(showAccounts) Surface(color=Color(0xFFEAF0E7),shape=RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                        Text("Estudiante · estudiante / Aprendo123")
                        Text("Docente · docente / Profe123")
                        Text("UTP · utp / Utp12345")
                    }
                }
            }
            Text("Escuela Escritora Marcela Paz",style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun LoginWelcome(modifier: Modifier,wide: Boolean) {
    Surface(modifier,color=Color(0xFFEAF0E7),shape=RoundedCornerShape(28.dp)) {
        Box(Modifier.fillMaxWidth().height(if(wide) 360.dp else 160.dp)) {
            Image(painterResource(R.drawable.explora_hero),null,Modifier.align(Alignment.BottomEnd).width(if(wide) 580.dp else 320.dp).height(if(wide) 290.dp else 160.dp))
            Column(Modifier.align(if(wide) Alignment.TopStart else Alignment.CenterStart).width(if(wide) 300.dp else 188.dp).padding(if(wide) 28.dp else 20.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("Aprende a tu ritmo",style=if(wide) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge)
                Text("Segundo Básico",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun LoginForm(state: SchoolUiState,username: String,onUsername: (String)->Unit,password: String,onPassword: (String)->Unit,visible: Boolean,onVisibility: ()->Unit,onSubmit: ()->Unit,modifier: Modifier) {
    Surface(modifier,shape=RoundedCornerShape(28.dp),color=Color.White) {
        Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text("Iniciar sesión",style=MaterialTheme.typography.headlineMedium)
            Text("Ingresa con tu usuario y clave.",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(username,onUsername,Modifier.fillMaxWidth(),label={Text("Usuario")},singleLine=true,enabled=!state.busy,shape=RoundedCornerShape(14.dp),
                keyboardOptions=KeyboardOptions(imeAction=ImeAction.Next),isError=state.loginErrors["username"]!=null,
                supportingText=state.loginErrors["username"]?.let {error -> {Text(error)}})
            OutlinedTextField(password,onPassword,Modifier.fillMaxWidth(),label={Text("Clave")},singleLine=true,enabled=!state.busy,shape=RoundedCornerShape(14.dp),
                visualTransformation=if(visible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password,imeAction=ImeAction.Done),keyboardActions=KeyboardActions(onDone={onSubmit()}),
                isError=state.loginErrors["password"]!=null,supportingText=state.loginErrors["password"]?.let {error -> {Text(error)}},
                trailingIcon={TextButton(onClick=onVisibility,enabled=!state.busy) {Text(if(visible) "Ocultar" else "Ver")}})
            Button(onClick=onSubmit,enabled=!state.busy,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp),shape=RoundedCornerShape(16.dp)) {
                Text(if(state.busy) "Ingresando…" else "Entrar",style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)
            }
        }
    }
}
