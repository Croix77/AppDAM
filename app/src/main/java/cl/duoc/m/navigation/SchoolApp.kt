package cl.duoc.m.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import cl.duoc.m.model.Role
import cl.duoc.m.ui.HomeScreen
import cl.duoc.m.ui.components.*
import cl.duoc.m.ui.screens.*
import cl.duoc.m.viewmodel.SchoolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolApp(wide: Boolean,vm: SchoolViewModel=viewModel()) {
    val s by vm.state.collectAsState()
    val snackbar=remember { SnackbarHostState() }
    LaunchedEffect(s.message) { s.message?.let { snackbar.showSnackbar(it);vm.dismissMessage() } }
    key(s.user?.id) {
        val nav=rememberNavController()
        val entry by nav.currentBackStackEntryAsState()
        val route=entry?.destination?.route
        Scaffold(topBar={TopAppBar(title={Text("MPAZ PRO 2.º")},navigationIcon={if(s.user!=null && route!="home") TextButton(onClick={nav.popBackStack()},enabled=!s.busy) {Text("← Atrás")}},actions={if(s.user!=null) TextButton(onClick=vm::logout,enabled=!s.busy) {Text("Salir")}})},snackbarHost={SnackbarHost(snackbar)}) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                if(s.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                if(s.user==null) LoginScreen(s,vm::login)
                else {
                    val user=s.user!!
                    NavHost(nav,"home",Modifier.weight(1f)) {
                        composable("home") {
                            if(user.role==Role.ESTUDIANTE) HomeScreen(user,s.lessons,wide,{vm.selectLesson(it);nav.navigate("lesson")},{s.lessons.firstOrNull()?.let {vm.selectLesson(it.id);nav.navigate("lesson")}})
                            else StaffHome(s,{vm.selectStudent(it);nav.navigate("student")},{vm.selectLesson(it);nav.navigate("editor")},{vm.newLesson();nav.navigate("editor")},{vm.loadAudit();nav.navigate("audit")})
                        }
                        composable("lesson") { if(user.role==Role.ESTUDIANTE) s.lesson?.let { LessonScreen(s,{nav.navigate("practice")},{vm.startTest();nav.navigate("test")}) } }
                        composable("practice") { if(user.role==Role.ESTUDIANTE) PracticeScreen(s,vm::answerPractice) }
                        composable("test") { if(user.role==Role.ESTUDIANTE) TestScreen(s,vm::choose,vm::submitTest) }
                        composable("student") { if(user.role==Role.DOCENTE) StudentManagement(s,vm,{student,lesson -> vm.loadReport(student,lesson);nav.navigate("report")},{nav.navigate("photo")}) }
                        composable("report") { if(user.role==Role.DOCENTE) ReportScreen(s,vm,vm::exportReport) }
                        composable("editor") { if(user.role==Role.UTP) LessonEditor(s,vm) }
                        composable("audit") { if(user.role==Role.UTP) Page("Historial UTP","Últimos 100 cambios locales") {s.audit.forEach {Text("${it.date} · ${it.actor}\n${it.action}")} } }
                        composable("photo") { if(user.role==Role.DOCENTE) Page("Imagen del estudiante") {Notice("La captura y galería se incorporan en el próximo bloque.")} }
                    }
                }
            }
        }
    }
}
