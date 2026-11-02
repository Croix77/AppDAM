package cl.duoc.m.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.m.model.*
import cl.duoc.m.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SchoolUiState(
    val busy: Boolean = false, val user: User? = null, val loginErrors: Map<String,String> = emptyMap(),
    val message: String? = null, val lessons: List<Lesson> = emptyList(), val students: List<User> = emptyList(),
    val lesson: Lesson? = null, val practice: List<Question> = emptyList(), val test: List<Question> = emptyList(),
    val answers: List<Answer> = emptyList(), val attempts: List<Attempt> = emptyList(), val assignment: Assignment? = null,
    val unavailable: String? = null, val progress: Int = 0, val choices: Map<Long,Int> = emptyMap(),
    val review: List<Pair<Question,Answer>> = emptyList(), val report: Report? = null,
    val audit: List<AuditEntry> = emptyList(), val exportedText: String? = null,
    val student: User? = null, val assignments: List<Assignment> = emptyList(), val largeText: Boolean = false,
    val activityFeedback: Map<Long,String> = emptyMap(), val lessonProgress: Map<Long,Int> = emptyMap(),
    val testFinished: Boolean = false
)

class SchoolViewModel(application: Application) : AndroidViewModel(application) {
    private val database=SchoolDatabase(application)
    private val repository=SchoolRepository(database)
    private val _state=MutableStateFlow(SchoolUiState())
    val state=_state.asStateFlow()
    private val preferences=ReadingPreferences(application)
    init {viewModelScope.launch {preferences.largeText.collect {value -> _state.update {it.copy(largeText=value)}}}}
    fun toggleText()=launchWork {preferences.save(!_state.value.largeText)}
    private fun actor()=_state.value.user?.id ?: error("Inicia sesión")
    private fun launchWork(work: suspend () -> Unit) {
        if(_state.value.busy) return
        _state.update { it.copy(busy=true,message=null) }
        viewModelScope.launch {
            try { withContext(Dispatchers.IO) { work() } }
            catch (e: Exception) { _state.update { it.copy(message=e.message ?: "No pudimos completar la acción. Intenta otra vez.") } }
            finally { _state.update { it.copy(busy=false) } }
        }
    }
    fun login(username: String,password: String) {
        val errors=FormRules.login(username,password)
        _state.update { it.copy(loginErrors=errors,message=null) }
        if(errors.isNotEmpty()) return
        launchWork {
            val user=repository.login(username,password)
            if(user==null) _state.update { it.copy(loginErrors=mapOf("password" to "Usuario o clave incorrectos")) }
            else { _state.value=SchoolUiState(user=user,busy=true,largeText=_state.value.largeText); refreshData() }
        }
    }
    fun logout() { if(!_state.value.busy) _state.value=SchoolUiState(largeText=_state.value.largeText) }
    fun dismissMessage() { _state.update { it.copy(message=null) } }
    fun showMessage(message: String) { _state.update { it.copy(message=message) } }
    private fun refreshData() {
        val user=repository.getUser(actor()) ?: error("Usuario no encontrado")
        val lessons=repository.lessons(user.id)
        val students=if(user.role==Role.DOCENTE) repository.students(user.id) else emptyList()
        val progress=if(user.role==Role.ESTUDIANTE) lessons.associate {it.id to repository.progress(user.id,it.id)} else emptyMap()
        _state.update { it.copy(user=user,lessons=lessons,students=students,lessonProgress=progress) }
        _state.value.lesson?.id?.let { loadLessonData(it) }
    }
    fun refresh()=launchWork { refreshData() }
    fun newLesson() { _state.update { it.copy(lesson=null,practice=emptyList(),test=emptyList()) } }
    fun selectStudent(id: Long)=launchWork {
        require(_state.value.user?.role==Role.DOCENTE && _state.value.students.any { it.id==id }) { "Acceso reservado al docente asignado" }
        _state.update { it.copy(student=repository.getUser(id),assignments=it.lessons.mapNotNull { l -> repository.assignment(id,l.id) }) }
    }
    fun selectLesson(id: Long)=launchWork { _state.update { it.copy(choices=emptyMap(),report=null,activityFeedback=emptyMap(),testFinished=false) }; loadLessonData(id) }
    private fun loadLessonData(id: Long) {
        val user=_state.value.user ?: return
        val lesson=repository.getLesson(id)
        if(user.role==Role.ESTUDIANTE) {
            require(repository.lessons(user.id).any { it.id==id }) { "Esta unidad no está asignada" }
            _state.update { it.copy(lesson=lesson,practice=repository.questions(id,false),test=repository.questions(id,true),answers=repository.practice(user.id,id),attempts=repository.attempts(user.id,id),assignment=repository.assignment(user.id,id),unavailable=repository.available(user.id,id),progress=repository.progress(user.id,id),review=repository.testReview(user.id,id)) }
        } else _state.update { it.copy(lesson=lesson,practice=repository.questions(id,false),test=repository.questions(id,true)) }
        if(user.role==Role.ESTUDIANTE) _state.update {it.copy(lessonProgress=it.lessonProgress+(id to it.progress))}
    }
    fun answerPractice(questionId: Long,choice: Int)=launchWork {
        val id=_state.value.lesson?.id ?: return@launchWork
        repository.savePractice(actor(),id,questionId,choice)
        loadLessonData(id)
    }
    fun submitActivity(questionId: Long,response: String)=launchWork {
        val s=_state.value
        val q=s.practice.firstOrNull {it.id==questionId} ?: error("Actividad no encontrada")
        if(FormRules.activityResponse(q,response)) {
            repository.savePractice(actor(),q.lessonId,q.id,q.correct)
            _state.update {it.copy(activityFeedback=it.activityFeedback-questionId)}
            loadLessonData(q.lessonId)
        } else {
            _state.update {it.copy(activityFeedback=it.activityFeedback+(questionId to "Revisa tu respuesta. ${q.explanation}"))}
        }
    }
    fun choose(questionId: Long,choice: Int) { if(!_state.value.busy) _state.update { it.copy(choices=it.choices+(questionId to choice)) } }
    fun startTest() { if(!_state.value.busy) _state.update { it.copy(choices=emptyMap(),testFinished=false) } }
    fun showTestResult() { if(!_state.value.busy && _state.value.attempts.isNotEmpty()) _state.update {it.copy(testFinished=true)} }
    fun submitTest()=launchWork {
        val s=_state.value
        repository.submitTest(actor(),s.lesson!!.id,s.choices)
        loadLessonData(s.lesson.id)
        _state.update { it.copy(choices=emptyMap(),testFinished=true,message=null) }
    }
    fun assignmentErrors(count: String,start: String,end: String): Map<String,String> = buildMap {
        if((count.toIntOrNull() ?: 0) !in 1..5) put("count","Escribe un número entre 1 y 5")
        val first=runCatching { java.time.LocalDate.parse(start) }.getOrNull()
        val last=runCatching { java.time.LocalDate.parse(end) }.getOrNull()
        if(first==null) put("start","Usa una fecha válida: AAAA-MM-DD")
        if(last==null) put("end","Usa una fecha válida: AAAA-MM-DD")
        if(first!=null && last!=null && last<first) put("end","Debe ser igual o posterior a la fecha inicial")
    }
    fun assign(studentId: Long,lessonId: Long,count: String,start: String,end: String) = launchWork {
        require(assignmentErrors(count,start,end).isEmpty()) { "Revisa los campos marcados" }
        repository.assign(actor(),studentId,lessonId,count.toInt(),start,end)
        _state.update { it.copy(message="Unidad asignada correctamente",assignments=it.lessons.mapNotNull { l -> repository.assignment(studentId,l.id) }) }
    }
    fun loadReport(studentId: Long,lessonId: Long)=launchWork { _state.update { it.copy(report=repository.report(actor(),studentId,lessonId),exportedText=null) } }
    fun noteErrors(note: String)=if(note.isBlank()) "Escribe una observación pedagógica" else null
    fun saveNote(note: String,validated: Boolean)=launchWork {
        val r=_state.value.report ?: return@launchWork
        require(noteErrors(note)==null) { "Escribe una observación pedagógica" }
        repository.saveNote(actor(),r.student.id,r.lesson.id,note,validated)
        _state.update { it.copy(report=repository.report(actor(),r.student.id,r.lesson.id),message="Observación guardada") }
    }
    fun exportReport()=launchWork {
        val r=_state.value.report ?: return@launchWork
        _state.update { it.copy(exportedText=repository.reportText(actor(),r.student.id,r.lesson.id)) }
    }
    fun clearExport() { _state.update { it.copy(exportedText=null) } }
    fun lessonErrors(values: List<String>): Map<Int,String> = values.mapIndexedNotNull { index,value -> if(value.isBlank()) index to "Completa este campo" else null }.toMap()
    fun saveLesson(id: Long?,subject: String,title: String,objective: String,text: String,example: String,origin: String)=launchWork {
        val selected=repository.saveLesson(actor(),id,subject,title,objective,text,example,origin)
        refreshData(); loadLessonData(selected)
        _state.update { it.copy(message="Borrador guardado") }
    }
    fun setStatus(id: Long,target: UnitStatus)=launchWork { repository.setStatus(actor(),id,target);refreshData();_state.update { it.copy(message="Estado actualizado a ${target.name.lowercase().replace('_',' ')}") } }
    fun questionErrors(prompt: String,options: List<String>,explanation: String): Map<Int,String> {
        val errors=lessonErrors(listOf(prompt)+options+explanation).toMutableMap()
        if(options.map { it.trim().lowercase() }.distinct().size!=3) options.indices.forEach { errors[it+1]="Usa alternativas diferentes" }
        return errors
    }
    fun saveQuestion(prompt: String,options: List<String>,correct: Int,explanation: String,finalTest: Boolean)=launchWork {
        val id=_state.value.lesson!!.id
        repository.saveQuestion(actor(),id,prompt,options,correct,explanation,finalTest)
        loadLessonData(id);_state.update { it.copy(message="Pregunta agregada") }
    }
    fun loadAudit()=launchWork { _state.update { it.copy(audit=repository.auditLog(actor())) } }
    fun savePhoto(studentId: Long,path: String)=launchWork {
        require(_state.value.user?.role==Role.DOCENTE && _state.value.students.any {it.id==studentId}) {"Acceso reservado al docente asignado"}
        val old=repository.getUser(studentId)?.photo
        val stored=MediaFiles.save(getApplication(),studentId,path)
        try {repository.updatePhoto(actor(),studentId,stored)} catch(e: Exception) {java.io.File(stored).delete();throw e}
        old?.takeIf {it.isNotBlank()}?.let {java.io.File(it).delete()}
        refreshData();_state.update {it.copy(student=repository.getUser(studentId),message="Imagen guardada")}
    }
    fun removePhoto(studentId: Long)=launchWork {
        val old=repository.getUser(studentId)?.photo
        repository.updatePhoto(actor(),studentId,"")
        old?.takeIf {it.isNotBlank()}?.let {java.io.File(it).delete()}
        refreshData();_state.update {it.copy(student=repository.getUser(studentId),message="Avatar restaurado")}
    }
}
