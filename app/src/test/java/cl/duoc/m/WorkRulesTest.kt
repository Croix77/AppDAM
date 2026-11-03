package cl.duoc.m

import cl.duoc.m.model.WorkRules
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class WorkRulesTest {
    @Test fun gradesAcceptOneDecimalAndNeverRoundInvalidValues() {
        assertEquals(10,WorkRules.gradeTenths("1"))
        assertEquals(65,WorkRules.gradeTenths("6,5"))
        assertEquals(70,WorkRules.gradeTenths("7.0"))
        listOf("0,9","7,1","6,55","NaN","nota","-1","6,5.0").forEach {assertNull(it,WorkRules.gradeTenths(it))}
    }
    @Test fun publishingRequiresGradeAndFeedbackButADraftCanBePartial() {
        assertTrue(WorkRules.review("","Comentario provisional",false).isEmpty())
        assertEquals(setOf("grade","feedback"),WorkRules.review("","",true).keys)
        assertTrue(WorkRules.review("6,0","Buen trabajo.",true).isEmpty())
        assertNotNull(WorkRules.response(" "))
        assertNotNull(WorkRules.response("a".repeat(4001)))
    }
    @Test fun dueDatesAreRealAndCannotStartInThePast() {
        assertTrue(WorkRules.creation("Relato","Escribe un relato",LocalDate.now().toString()).isEmpty())
        assertTrue(WorkRules.creation("Relato","Instrucciones","2026-02-30").containsKey("due"))
        assertTrue(WorkRules.creation("Relato","Instrucciones",LocalDate.now().minusDays(1).toString()).containsKey("due"))
    }
}
