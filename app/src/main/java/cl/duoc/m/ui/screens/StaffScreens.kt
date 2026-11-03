package cl.duoc.m.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.m.model.*
import cl.duoc.m.ui.components.*
import cl.duoc.m.viewmodel.*
import java.time.LocalDate
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StaffHome(s: SchoolUiState,onStudent: (Long)->Unit,onEdit: (Long)->Unit,onNew: ()->Unit,onAudit: ()->Unit,onCourse: (String)->Unit={}) {
    val user=s.user ?: return
    Page(if(user.role==Role.DOCENTE) "Mis cursos" else "Gestión pedagógica", user.name) {
        if(user.role==Role.DOCENTE) {
            s.students.groupBy {it.course}.toSortedMap().forEach {(course,students) ->
                Card(onClick={onCourse(course)},enabled=!s.busy,shape=androidx.compose.foundation.shape.RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=androidx.compose.ui.graphics.Color(0xFFEAF0E7))) {
                    Column(Modifier.fillMaxWidth().padding(22.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
                        Text(course,style=MaterialTheme.typography.headlineMedium)
                        val count=s.works.count {it.course==course}
                        Text("${students.size} ${if(students.size==1) "estudiante" else "estudiantes"} · $count ${if(count==1) "trabajo" else "trabajos"}",style=MaterialTheme.typography.bodyMedium)
                        Text("Abrir curso →",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary)
                    }
                }
            }
            if(s.students.isEmpty()) Notice("No tienes cursos asignados.")
        } else {
            Text("Unidades de aprendizaje",style=MaterialTheme.typography.titleMedium)
            Action("Crear unidad",!s.busy,onNew)
            OutlinedButton(onClick=onAudit,enabled=!s.busy) { Text("Consultar historial de cambios") }
            s.lessons.forEach { lesson -> Panel(lesson.title) {
                Text("${lesson.subject} · ${lesson.status.name.replace('_',' ')}")
                Text(lesson.objective)
                Action("Gestionar unidad",!s.busy) {onEdit(lesson.id)}
            } }
        }
    }
}

@Composable
fun StudentManagement(s: SchoolUiState,vm: SchoolViewModel,onReport: (Long,Long)->Unit,onPhoto: ()->Unit) {
    val student=s.student ?: return
    Page(student.name,student.course) {
        Action("Administrar imagen del estudiante",!s.busy,onPhoto)
        s.lessons.forEach { lesson ->
            val a=s.assignments.firstOrNull { it.lessonId==lesson.id }
            var count by rememberSaveable(student.id,lesson.id,a?.maxAttempts) { mutableStateOf((a?.maxAttempts ?: 2).toString()) }
            var start by rememberSaveable(student.id,lesson.id,a?.start) { mutableStateOf(a?.start ?: LocalDate.now().toString()) }
            var end by rememberSaveable(student.id,lesson.id,a?.end) { mutableStateOf(a?.end ?: LocalDate.now().plusMonths(1).toString()) }
            var submitted by rememberSaveable(student.id,lesson.id) { mutableStateOf(false) }
            val errors=if(submitted) vm.assignmentErrors(count,start,end) else emptyMap()
            Panel("${lesson.subject}: ${lesson.title}") {
                Text("Estado UTP: ${lesson.status.name.replace('_',' ')}")
                Text(if(a!=null) "Ya asignada al estudiante" else "Sin asignar")
                Field("Máximo de intentos (1 a 5)",count,{count=it},errors["count"])
                Field("Disponible desde (AAAA-MM-DD)",start,{start=it},errors["start"])
                Field("Disponible hasta (AAAA-MM-DD)",end,{end=it},errors["end"])
                Action("Guardar asignación",!s.busy && lesson.status in listOf(UnitStatus.AUTORIZADO,UnitStatus.PUBLICADO)) {
                    submitted=true
                    if(vm.assignmentErrors(count,start,end).isEmpty()) vm.assign(student.id,lesson.id,count,start,end)
                }
                OutlinedButton(onClick={onReport(student.id,lesson.id)},enabled=!s.busy) {Text("Consultar informe pedagógico")}
            }
        }
    }
}

@Composable
fun ReportScreen(s: SchoolUiState,vm: SchoolViewModel,onExport: ()->Unit) {
    val r=s.report ?: return
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    var export by rememberSaveable {mutableStateOf("")}
    val saveFile=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        if(uri!=null) scope.launch {
            try {
                withContext(Dispatchers.IO) {requireNotNull(context.contentResolver.openOutputStream(uri)).bufferedWriter(Charsets.UTF_8).use {it.write(export)}}
                vm.showMessage("Informe descargado")
            } catch (_: Exception) {vm.showMessage("No pudimos guardar el informe. Elige otra ubicación.")}
            finally {export=""}
        } else {export="";vm.showMessage("Descarga cancelada")}
    }
    LaunchedEffect(s.exportedText) {s.exportedText?.let {export=it;vm.clearExport();saveFile.launch("informe_${r.student.id}_${r.lesson.id}.txt")}}
    var note by rememberSaveable(r.student.id,r.lesson.id,r.note) {mutableStateOf(r.note)}
    var validated by rememberSaveable(r.student.id,r.lesson.id,r.validated) {mutableStateOf(r.validated)}
    var submitted by rememberSaveable {mutableStateOf(false)}
    Page("Informe pedagógico","Reservado al docente · ${r.student.name}") {
        Panel(r.lesson.title) {
            Text("Asignatura: ${r.lesson.subject}\nObjetivo: ${r.lesson.objective}")
            Text("Avance: ${r.progress}%")
            if(r.attempts.isEmpty()) Text("Todavía no hay pruebas rendidas.")
            r.attempts.forEach { Text("Intento ${it.number}: ${it.score}/${it.total} · ${it.date}") }
        }
        if(r.answers.isNotEmpty()) Panel("Respuestas del último intento") {
            r.answers.forEach { (q,a) -> Text(q.prompt,style=MaterialTheme.typography.titleMedium);Text("Seleccionó: ${q.options[a.choice]}");Notice("${if(a.correct) "Correcta" else "Necesita refuerzo"}. ${q.explanation}",!a.correct) }
        }
        Panel("Observación del docente") {
            Field("Fortalezas y aspectos a reforzar",note,{note=it;validated=false},if(submitted) vm.noteErrors(note) else null,false)
            Row { Checkbox(validated,{validated=it});Text("Validar esta observación") }
            Action("Guardar observación",!s.busy) { submitted=true;if(vm.noteErrors(note)==null) vm.saveNote(note,validated) }
            Text("Estado guardado: ${if(r.validated) "validado" else "pendiente de validación"}")
        }
        Action("Descargar informe de texto",!s.busy,onExport)
    }
}

@Composable
fun LessonEditor(s: SchoolUiState,vm: SchoolViewModel) {
    val lesson=s.lesson
    var subject by rememberSaveable(lesson?.id) {mutableStateOf(lesson?.subject ?: "Lenguaje")}
    var title by rememberSaveable(lesson?.id) {mutableStateOf(lesson?.title ?: "")}
    var objective by rememberSaveable(lesson?.id) {mutableStateOf(lesson?.objective ?: "")}
    var text by rememberSaveable(lesson?.id) {mutableStateOf(lesson?.explanation ?: "")}
    var example by rememberSaveable(lesson?.id) {mutableStateOf(lesson?.example ?: "")}
    var origin by rememberSaveable(lesson?.id) {mutableStateOf(lesson?.origin ?: "")}
    var submitted by rememberSaveable(lesson?.id) {mutableStateOf(false)}
    val editable=lesson==null || lesson.status==UnitStatus.BORRADOR
    val errors=if(submitted) vm.lessonErrors(listOf(title,objective,text,example,origin)) else emptyMap()
    Page(if(lesson==null) "Nueva unidad" else "Gestionar unidad","Segundo Básico") {
        if(lesson!=null) {
            Notice("Estado: ${lesson.status.name.replace('_',' ')}")
            val targets=when(lesson.status) {
                UnitStatus.BORRADOR -> listOf(UnitStatus.EN_REVISION)
                UnitStatus.EN_REVISION -> listOf(UnitStatus.AUTORIZADO,UnitStatus.BORRADOR)
                UnitStatus.AUTORIZADO -> listOf(UnitStatus.PUBLICADO,UnitStatus.BORRADOR)
                UnitStatus.PUBLICADO -> listOf(UnitStatus.DESHABILITADO)
                UnitStatus.DESHABILITADO -> listOf(UnitStatus.PUBLICADO,UnitStatus.BORRADOR)
            }
            targets.forEach { target -> OutlinedButton(onClick={vm.setStatus(lesson.id,target)},enabled=!s.busy) {Text("Pasar a ${target.name.lowercase().replace('_',' ')}")} }
        }
        if(editable) Panel("Contenido") {
            Text("Asignatura")
            listOf("Lenguaje","Matemática","Ciencias Naturales","Historia y Geografía").forEach { option -> Row {RadioButton(subject==option,{subject=option});Text(option)} }
            Field("Título",title,{title=it},errors[0])
            Field("Objetivo de aprendizaje (descripción)",objective,{objective=it},errors[1],false)
            Field("Explicación escrita",text,{text=it},errors[2],false)
            Field("Ejemplo guiado",example,{example=it},errors[3],false)
            Field("Autoría y permiso de uso",origin,{origin=it},errors[4],false)
            Action("Guardar borrador",!s.busy) {
                submitted=true
                if(vm.lessonErrors(listOf(title,objective,text,example,origin)).isEmpty()) vm.saveLesson(lesson?.id,subject,title,objective,text,example,origin)
            }
        } else Panel(lesson!!.title) {Text(lesson.objective);Text(lesson.explanation);Text(lesson.example);Text(lesson.origin)}
        if(lesson!=null) {
            Panel("Preguntas de práctica (${s.practice.size}) y prueba (${s.test.size})") {
                (s.practice+s.test).forEach { q -> Text("${if(q.finalTest) "Prueba" else "Práctica"}: ${q.prompt}\nCorrecta: ${q.options[q.correct]}") }
                Text("Las preguntas guardadas no se eliminan. Agrega nuevas en estado borrador.")
            }
            if(editable) key(s.practice.size+s.test.size) {QuestionEditor(s,vm)}
        }
    }
}

@Composable
private fun QuestionEditor(s: SchoolUiState,vm: SchoolViewModel) {
    var prompt by rememberSaveable(s.lesson?.id) {mutableStateOf("")}
    var a by rememberSaveable(s.lesson?.id) {mutableStateOf("")}
    var b by rememberSaveable(s.lesson?.id) {mutableStateOf("")}
    var c by rememberSaveable(s.lesson?.id) {mutableStateOf("")}
    var explanation by rememberSaveable(s.lesson?.id) {mutableStateOf("")}
    var correct by rememberSaveable(s.lesson?.id) {mutableStateOf(0)}
    var finalTest by rememberSaveable(s.lesson?.id) {mutableStateOf(false)}
    var submitted by rememberSaveable(s.lesson?.id) {mutableStateOf(false)}
    val options=listOf(a,b,c)
    val errors=if(submitted) vm.questionErrors(prompt,options,explanation) else emptyMap()
    Panel("Agregar pregunta") {
        Field("Pregunta",prompt,{prompt=it},errors[0],false)
        Field("Alternativa A",a,{a=it},errors[1]);Field("Alternativa B",b,{b=it},errors[2]);Field("Alternativa C",c,{c=it},errors[3])
        Text("Respuesta correcta")
        listOf("A","B","C").forEachIndexed { i,label -> Row {RadioButton(correct==i,{correct=i});Text(label)} }
        Field("Explicación de la respuesta",explanation,{explanation=it},errors[4],false)
        Row {Checkbox(finalTest,{finalTest=it});Text("Usar en prueba final (sin marcar: práctica)")}
        Action("Agregar pregunta al borrador",!s.busy) {submitted=true;if(vm.questionErrors(prompt,options,explanation).isEmpty()) vm.saveQuestion(prompt,options,correct,explanation,finalTest)}
    }
}
