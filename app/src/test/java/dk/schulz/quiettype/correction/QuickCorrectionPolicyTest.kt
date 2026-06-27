package dk.schulz.quiettype.correction

import org.junit.Assert.assertEquals
import org.junit.Test

class QuickCorrectionPolicyTest {
    @Test
    fun normalizeSpacingTrimsAndCollapsesWhitespace() {
        assertEquals(
            "hello world this is quiettype",
            QuickCorrectionPolicy.apply("  hello   world\nthis\t is quiettype  ", QuickCorrectionAction.NormalizeSpacing),
        )
    }

    @Test
    fun capitalizeFirstLetterKeepsRestOfTextUntouched() {
        assertEquals(
            "Hello WORLD",
            QuickCorrectionPolicy.apply("hello WORLD", QuickCorrectionAction.CapitalizeFirst),
        )
    }

    @Test
    fun sentenceCaseMakesReadableSentence() {
        assertEquals(
            "Hello world from quiettype",
            QuickCorrectionPolicy.apply("HELLO WORLD FROM QUIETTYPE", QuickCorrectionAction.SentenceCase),
        )
    }

    @Test
    fun addPeriodAvoidsDuplicateTerminalPunctuation() {
        assertEquals("Hello world.", QuickCorrectionPolicy.apply("Hello world", QuickCorrectionAction.AddPeriod))
        assertEquals("Hello world!", QuickCorrectionPolicy.apply("Hello world!", QuickCorrectionAction.AddPeriod))
    }

    @Test
    fun suggestedActionsAreSafeAndOrderedForDictationText() {
        assertEquals(
            listOf(
                QuickCorrectionAction.NormalizeSpacing,
                QuickCorrectionAction.CapitalizeFirst,
                QuickCorrectionAction.SentenceCase,
                QuickCorrectionAction.AddPeriod,
            ),
            QuickCorrectionPolicy.suggestedActions("  HELLO   WORLD "),
        )
    }
}
