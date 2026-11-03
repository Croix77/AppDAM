package cl.duoc.m.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.m.model.*
import cl.duoc.m.ui.components.*
import cl.duoc.m.viewmodel.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private fun dateLabel(value: String)=runCatching {LocalDate.parse(value).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}.getOrDefault(value)
private fun timeLabel(value: String)=runCatching {LocalDateTime.parse(value,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm"))}.getOrDefault(value)

@Composable
fun CourseScreen(s: SchoolUiState,onStudent: (Long)->Unit,onWork: (Long)->Unit,onNew: ()->Unit) {
    val students=s.students.filter {it.course==s.selectedCourse}
    val works=s.works.filter {it.course==s.selectedCourse}
    var tab by rememberSaveable(s.selectedCourse) {mutableStateOf("Estudiantes")}
    Page(s.selectedCourse,"${students.size} ${if(students.size==1) "estudiante" else "estudiantes"} · ${works.size} ${if(works.size==1) "trabajo" else "trabajos"}") {
        Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            listOf("Estudiantes","Trabajos").forEach {label -> FilterChip(selected=tab==label,onClick={tab=label},label={Text(label)},modifier=Modifier.heightIn(min=48.dp))}
        }
        if(tab=="Estudiantes") {
            students.forEach {student ->
                Surface(color=Color.White,shape=RoundedCornerShape(22.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                            Avatar(student.photo,student.name,size=54.dp)
                            Column(Modifier.weight(1f)) {Text(student.name,style=MaterialTheme.typography.titleMedium);Text(student.username,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)}
                        }
                        Action("Ver perfil y aprendizaje",!s.busy) {onStudent(student.id)}
                    }
                }
            }
        } else {
            Action("Crear trabajo",!s.busy,onNew)
            if(works.isEmpty()) WorkEmpty("Todavía no hay trabajos","Crea el primer trabajo para este curso.")
            works.forEach {WorkCard(it,null,true,s.busy) {onWork(it.id)}}
        }
    }
}

@Composable
fun WorkListScreen(s: SchoolUiState,onWork: (Long)->Unit) {
    val pending=s.works.count {s.ownSubmissions[it.id]?.delivered!=true}
    Page("Mis trabajos",s.user?.course.orEmpty()) {
        Surface(color=Color(0xFFE6E0EF),shape=RoundedCornerShape(22.dp)) {
            Row(Modifier.fillMaxWidth().padding(22.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(18.dp)) {
                Text(pending.toString(),style=MaterialTheme.typography.headlineLarge,color=MaterialTheme.colorScheme.primary)
                Text(if(pending==1) "trabajo pendiente" else "trabajos pendientes",style=MaterialTheme.typography.titleMedium)
            }
        }
        if(s.works.isEmpty()) WorkEmpty("Todo al día","Aquí aparecerán los trabajos de tu docente.")
        s.works.forEach {WorkCard(it,s.ownSubmissions[it.id],false,s.busy) {onWork(it.id)}}
    }
}

@Composable
private fun WorkEmpty(title: String,message: String) {
    Surface(color=Color.White,shape=RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(28.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Text(title,style=MaterialTheme.typography.titleLarge)
            Text(message,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WorkCard(task: SchoolWork,submission: WorkSubmission?,teacher: Boolean,busy: Boolean,onOpen: ()->Unit) {
    Card(onClick=onOpen,enabled=!busy,shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {
        Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                SubjectMark(task.subject,small=true)
                Text(task.subject,Modifier.weight(1f),style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
                if(!teacher) WorkStatus(submission?.status ?: "Pendiente")
            }
            Text(task.title,style=MaterialTheme.typography.titleLarge)
            Text("Entrega hasta ${dateLabel(task.dueDate)}",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            if(teacher) Text("${task.delivered}/${task.assigned} entregados · ${task.reviewed} ${if(task.reviewed==1) "revisado" else "revisados"}",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary)
            else if(submission?.published==true) Text("Nota ${submission.gradeLabel}",style=MaterialTheme.typography.titleMedium,color=MaterialTheme.colorScheme.primary)
            Text(if(teacher) "Ver entregas →" else "Abrir trabajo →",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun WorkStatus(label: String) {
    val paper=when(label) {"Revisado" -> Color(0xFFDCEFC5);"Entregado" -> Color(0xFFE2EBFA);else -> Color(0xFFFFEDCD)}
    Surface(color=paper,shape=RoundedCornerShape(30.dp)) {Text(label,Modifier.padding(horizontal=12.dp,vertical=7.dp),style=MaterialTheme.typography.labelMedium,color=Color(0xFF183C45))}
}

@Composable
private fun WorkHeading(task: SchoolWork) {
    Surface(color=Color(0xFFEAF0E7),shape=RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(22.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {SubjectMark(task.subject,small=true);Text(task.subject,style=MaterialTheme.typography.labelLarge)}
            Text(task.title,style=MaterialTheme.typography.headlineMedium)
            Text("${task.course} · Hasta ${dateLabel(task.dueDate)}",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun WorkEditor(s: SchoolUiState,vm: SchoolViewModel,onCreated: ()->Unit) {
    var subject by rememberSaveable {mutableStateOf("Lenguaje")}
    var title by rememberSaveable {mutableStateOf("")}
    var instructions by rememberSaveable {mutableStateOf("")}
    var due by rememberSaveable {mutableStateOf(LocalDate.now().plusDays(7).toString())}
    var submitted by rememberSaveable {mutableStateOf(false)}
    val errors=if(submitted) WorkRules.creation(title,instructions,due) else emptyMap()
    LaunchedEffect(s.work?.id) {if(s.work!=null) onCreated()}
    Page("Nuevo trabajo",s.selectedCourse) {
        Panel("Actividad para el curso") {
            Text("Asignatura",style=MaterialTheme.typography.labelLarge)
            WorkRules.subjects.chunked(2).forEach {row -> Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                row.forEach {option -> FilterChip(selected=subject==option,onClick={subject=option},label={Text(option)},modifier=Modifier.weight(1f).heightIn(min=48.dp))}
            }}
            Field("Título del trabajo",title,{title=it},errors["title"])
            OutlinedTextField(instructions,{instructions=it},Modifier.fillMaxWidth(),label={Text("Instrucciones")},minLines=4,maxLines=10,isError=errors["instructions"]!=null,supportingText=errors["instructions"]?.let {error -> {Text(error)}})
            Field("Fecha de entrega (AAAA-MM-DD)",due,{due=it},errors["due"])
        }
        val count=s.students.count {it.course==s.selectedCourse}
        Text("Se asignará a $count ${if(count==1) "estudiante" else "estudiantes"} de este curso.",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
        Action("Asignar trabajo al curso",!s.busy) {submitted=true;if(WorkRules.creation(title,instructions,due).isEmpty()) vm.createWork(subject,title,instructions,due)}
    }
}

@Composable
fun WorkDetailScreen(s: SchoolUiState,vm: SchoolViewModel,onReview: (Long)->Unit) {
    val task=s.work ?: return
    val teacher=s.user?.role==Role.DOCENTE
    var showInstructions by rememberSaveable(task.id) {mutableStateOf(false)}
    Page(if(teacher) "Entregas del curso" else "Mi trabajo") {
        WorkHeading(task)
        if(!teacher && s.submission?.published==true) {
            OutlinedButton(onClick={showInstructions=!showInstructions},modifier=Modifier.heightIn(min=48.dp)) {Text(if(showInstructions) "Ocultar instrucciones" else "Ver instrucciones")}
        }
        if(teacher || s.submission?.published!=true || showInstructions) Panel("Instrucciones") {Text(task.instructions,style=MaterialTheme.typography.bodyLarge)}
        if(teacher) {
            Surface(color=Color(0xFFE2EBFA),shape=RoundedCornerShape(18.dp)) {
                Text("${task.delivered}/${task.assigned} entregados · ${task.reviewed} ${if(task.reviewed==1) "revisado" else "revisados"}",Modifier.fillMaxWidth().padding(20.dp),style=MaterialTheme.typography.titleMedium)
            }
            s.workStudents.forEach {item ->
                Surface(color=Color.White,shape=RoundedCornerShape(22.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                            Avatar(item.student.photo,item.student.name,size=48.dp)
                            Text(item.student.name,Modifier.weight(1f),style=MaterialTheme.typography.titleMedium)
                        }
                        WorkStatus(item.submission?.status ?: "Pendiente")
                        if(item.submission?.published==true) Text("Nota ${item.submission.gradeLabel}",style=MaterialTheme.typography.titleMedium)
                        if(item.submission?.delivered==true) Action(if(item.submission.published) "Ver evaluación" else "Revisar entrega",!s.busy) {onReview(item.student.id)}
                        else Text("Sin entrega",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else WorkResponse(s,vm)
    }
}

@Composable
private fun WorkResponse(s: SchoolUiState,vm: SchoolViewModel) {
    val task=s.work ?: return
    val submission=s.submission
    var response by rememberSaveable(task.id) {mutableStateOf(submission?.response.orEmpty())}
    var submitted by rememberSaveable(task.id) {mutableStateOf(false)}
    var confirm by remember {mutableStateOf(false)}
    val closed=LocalDate.now().toString()>task.dueDate
    val error=if(submitted) WorkRules.response(response) else null
    WorkStatus(submission?.status ?: "Pendiente")
    if(submission?.delivered==true) {
        if(submission.published) PublishedReview(submission)
        Panel("Tu respuesta") {Text(submission.response);Text("Entregado: ${timeLabel(submission.submittedAt)}",style=MaterialTheme.typography.labelMedium)}
        if(!submission.published) Notice("Evaluación pendiente.")
    } else {
        if(closed) Notice("El plazo de entrega terminó.",true)
        Panel("Tu respuesta") {
            OutlinedTextField(response,{response=it},Modifier.fillMaxWidth(),label={Text("Escribe tu respuesta")},minLines=6,maxLines=14,enabled=!s.busy && !closed,isError=error!=null,
                supportingText={Text(error ?: "${response.length}/4000 caracteres")})
            if(submission!=null) Text("Borrador guardado",style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.primary)
        }
        OutlinedButton(onClick={submitted=true;if(WorkRules.response(response)==null) vm.saveWorkResponse(response,false)},enabled=!s.busy && !closed,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp)) {Text("Guardar borrador")}
        Action("Entregar trabajo",!s.busy && !closed) {submitted=true;if(WorkRules.response(response)==null) confirm=true}
    }
    if(confirm) AlertDialog(onDismissRequest={confirm=false},title={Text("¿Entregar trabajo?")},text={Text("Tu respuesta quedará enviada y ya no podrás cambiarla.")},
        confirmButton={TextButton(onClick={confirm=false;vm.saveWorkResponse(response,true)}) {Text("Entregar")}},dismissButton={TextButton(onClick={confirm=false}) {Text("Seguir escribiendo")}})
}

@Composable
fun WorkReviewScreen(s: SchoolUiState,vm: SchoolViewModel) {
    val student=s.workStudent ?: return
    val submission=s.submission ?: return
    val task=s.work ?: return
    var grade by rememberSaveable(task.id,student.id,submission.gradeTenths) {mutableStateOf(submission.gradeLabel)}
    var feedback by rememberSaveable(task.id,student.id,submission.feedback) {mutableStateOf(submission.feedback)}
    var errors by remember {mutableStateOf(emptyMap<String,String>())}
    var confirm by remember {mutableStateOf(false)}
    Page("Revisar entrega",student.name) {
        WorkHeading(task)
        Panel("Respuesta del estudiante") {Text(submission.response,style=MaterialTheme.typography.bodyLarge);Text("Entregado: ${timeLabel(submission.submittedAt)}",style=MaterialTheme.typography.labelMedium)}
        if(submission.published) PublishedReview(submission)
        else {
            Panel("Evaluación") {
                OutlinedTextField(grade,{grade=it},Modifier.fillMaxWidth(),label={Text("Nota (1,0 a 7,0)")},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),isError=errors["grade"]!=null,supportingText=errors["grade"]?.let {error -> {Text(error)}})
                OutlinedTextField(feedback,{feedback=it},Modifier.fillMaxWidth(),label={Text("Comentario para el estudiante")},minLines=4,maxLines=10,isError=errors["feedback"]!=null,supportingText=errors["feedback"]?.let {error -> {Text(error)}})
            }
            OutlinedButton(onClick={errors=WorkRules.review(grade,feedback,false);if(errors.isEmpty()) vm.saveWorkReview(grade,feedback,false)},enabled=!s.busy,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp)) {Text("Guardar corrección")}
            Action("Publicar evaluación",!s.busy) {errors=WorkRules.review(grade,feedback,true);if(errors.isEmpty()) confirm=true}
        }
    }
    if(confirm) AlertDialog(onDismissRequest={confirm=false},title={Text("¿Publicar evaluación?")},text={Text("El estudiante verá la nota y tu comentario. Esta evaluación no podrá editarse.")},
        confirmButton={TextButton(onClick={confirm=false;vm.saveWorkReview(grade,feedback,true)}) {Text("Publicar")}},dismissButton={TextButton(onClick={confirm=false}) {Text("Seguir revisando")}})
}

@Composable
private fun PublishedReview(submission: WorkSubmission) {
    Surface(color=Color(0xFFDCEFC5),shape=RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text("Evaluación publicada",style=MaterialTheme.typography.titleMedium)
            Text("Nota ${submission.gradeLabel}",style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary)
            HorizontalDivider(color=Color(0xFFBCD49F))
            Text("Comentario del docente",style=MaterialTheme.typography.labelLarge)
            Text(submission.feedback,style=MaterialTheme.typography.bodyLarge)
        }
    }
}
