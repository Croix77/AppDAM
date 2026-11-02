package cl.duoc.m

import cl.duoc.m.model.FormRules
import org.junit.Assert.*
import org.junit.Test

class FormRulesTest {
    @Test fun numericAndSequenceActivitiesValidateResponses() {
        val numeric=cl.duoc.m.model.Question(1,1,"Suma",listOf("39","x","y"),0,"Explicación",false,cl.duoc.m.model.ActivityKind.NUMERO)
        assertTrue(FormRules.activityResponse(numeric," 39 "))
        assertFalse(FormRules.activityResponse(numeric,"38"))
        assertFalse(FormRules.activityResponse(numeric,"texto"))
        val sequence=numeric.copy(options=listOf("Primero → Luego → Final","x","y"),kind=cl.duoc.m.model.ActivityKind.ORDEN)
        assertTrue(FormRules.activityResponse(sequence,"Primero → Luego → Final"))
        assertFalse(FormRules.activityResponse(sequence,"Final → Primero → Luego"))
    }
    @Test fun emptyCredentialsHaveErrorsPerField() {assertEquals(setOf("username","password"),FormRules.login(" ","").keys)}
    @Test fun attemptLimitAndDateOrderAreValidated() {
        assertNotNull(FormRules.assignment("0","2026-01-01","2026-02-01"))
        assertNotNull(FormRules.assignment("6","2026-01-01","2026-02-01"))
        assertNotNull(FormRules.assignment("2","2026-02-30","2026-04-01"))
        assertNotNull(FormRules.assignment("2","2026-05-01","2026-04-01"))
        assertNull(FormRules.assignment("2","2026-01-01","2026-12-31"))
    }
    @Test fun questionRejectsAmbiguousOptions() {
        assertNotNull(FormRules.question("¿Qué ves?",listOf("Sol"," sol ","Luna"),"Una estrella"))
        assertNull(FormRules.question("¿Qué ves?",listOf("Sol","Tierra","Luna"),"Una estrella"))
    }
}
