package cl.duoc.m.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import cl.duoc.m.repository.MediaFiles
import cl.duoc.m.ui.components.*
import cl.duoc.m.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun Avatar(path: String,name: String,modifier: Modifier=Modifier) {
    var bitmap by remember(path) {mutableStateOf<android.graphics.Bitmap?>(null)}
    LaunchedEffect(path) {bitmap=withContext(Dispatchers.IO) {if(path.isBlank()) null else MediaFiles.decode(path)}}
    if(bitmap!=null) Image(bitmap!!.asImageBitmap(),"Imagen de $name",modifier.size(112.dp).clip(CircleShape),contentScale=ContentScale.Crop)
    else Surface(modifier.size(112.dp),shape=CircleShape,color=MaterialTheme.colorScheme.secondaryContainer) {Box(contentAlignment=androidx.compose.ui.Alignment.Center) {Text(name.takeLast(3).uppercase(),style=MaterialTheme.typography.headlineMedium)}}
}
@Composable
fun PhotoScreen(s: SchoolUiState,vm: SchoolViewModel) {
    val student=s.student ?: return
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    var cameraPath by rememberSaveable(student.id) {mutableStateOf("")}
    var pending by rememberSaveable(student.id) {mutableStateOf("")}
    var savedPhoto by rememberSaveable(student.id) {mutableStateOf(student.photo)}
    LaunchedEffect(student.photo) {if(savedPhoto!=student.photo) {pending="";savedPhoto=student.photo}}
    var error by remember {mutableStateOf<String?>(null)}
    var importing by remember {mutableStateOf(false)}
    val camera=rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if(success) pending=cameraPath else {File(cameraPath).delete();error="Captura cancelada. La imagen guardada no cambió."}
    }
    val gallery=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri!=null) scope.launch {
            importing=true
            try {pending=withContext(Dispatchers.IO) {MediaFiles.candidate(context,uri)};error=null}
            catch(e: Exception) {error=e.message ?: "No pudimos cargar la imagen"}
            finally {importing=false}
        }
    }
    Page("Imagen del estudiante",student.name) {
        Notice("Solo el docente administra esta imagen. Usa un avatar o una imagen ficticia para esta demostración.")
        Panel(if(pending.isBlank()) "Imagen actual" else "Vista previa sin guardar") {
            Avatar(pending.ifBlank {student.photo},student.name)
            Text("La foto es opcional. El estudiante no puede cambiarla.")
        }
        error?.let {Notice(it,true)}
        Action("Elegir desde galería",!s.busy && !importing) {runCatching {gallery.launch("image/*")}.onFailure {error="No hay una aplicación de galería disponible"}}
        Action("Tomar foto de prueba",!s.busy && !importing) {
            try {
                val directory=File(context.cacheDir,"camera").apply {mkdirs()}
                val file=File.createTempFile("capture_",".jpg",directory)
                cameraPath=file.path
                camera.launch(FileProvider.getUriForFile(context,"${context.packageName}.files",file))
            } catch (_: Exception) {error="No pudimos abrir la cámara. Puedes usar la galería."}
        }
        if(pending.isNotBlank()) {
            Action("Guardar esta imagen",!s.busy && !importing) {vm.savePhoto(student.id,pending)}
            OutlinedButton(onClick={pending=""},enabled=!s.busy) {Text("Cancelar vista previa")}
        }
        if(student.photo.isNotBlank()) OutlinedButton(onClick={pending="";vm.removePhoto(student.id)},enabled=!s.busy) {Text("Volver al avatar predeterminado")}
        if(importing) CircularProgressIndicator()
    }
}
