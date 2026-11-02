package cl.duoc.m.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

@Composable
fun Page(title: String, subtitle: String = "", content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),contentAlignment=Alignment.TopCenter) {
        Column(Modifier.widthIn(max=1000.dp).fillMaxWidth().padding(horizontal=20.dp).verticalScroll(rememberScrollState()).padding(vertical=24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
            Text(title,style=MaterialTheme.typography.headlineMedium)
            if(subtitle.isNotBlank()) Text(subtitle,style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
            content()
            Spacer(Modifier.height(24.dp))
        }
    }
}
@Composable
fun Panel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text(title,style=MaterialTheme.typography.titleLarge)
            content()
        }
    }
}
@Composable
fun Action(text: String,enabled: Boolean=true,onClick: () -> Unit) {
    Button(onClick,Modifier.fillMaxWidth().heightIn(min=52.dp),enabled=enabled,contentPadding=PaddingValues(12.dp)) { Text(text,fontSize=16.sp) }
}
@Composable
fun Field(label: String,value: String,onChange: (String)->Unit,error: String?=null,singleLine: Boolean=true,leadingIcon: ImageVector?=null) {
    OutlinedTextField(value,onChange,Modifier.fillMaxWidth(),label={Text(label)},singleLine=singleLine,isError=error!=null,
        leadingIcon=leadingIcon?.let {{Icon(it,contentDescription=null,tint=MaterialTheme.colorScheme.primary)}},
        supportingText=if(error!=null) ({Text(error)}) else null,
        trailingIcon=if(error!=null) ({Icon(Icons.Filled.Warning,contentDescription="Campo con error",tint=MaterialTheme.colorScheme.error)}) else null)
}
@Composable
fun Notice(text: String,error: Boolean=false) {
    Surface(color=if(error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,shape=MaterialTheme.shapes.medium) {
        Text(text,Modifier.fillMaxWidth().padding(16.dp),style=MaterialTheme.typography.bodyLarge)
    }
}
