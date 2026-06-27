package dk.schulz.quiettype.accessibility

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextCorrectionDraftTest {
    @Test
    fun correctionCleansSelectedTextInActiveEditableField() {
        val draft = TextCorrectionDraft.from(
            TextCorrectionRequest(
                focusedField = editableField(),
                existingText = "Please send HELLO   WORLD today",
                selectionStart = 12,
                selectionEnd = 25,
            ),
        )

        assertTrue(draft.canCorrect)
        assertEquals("Please send Hello world. today", draft.textToSet)
        assertEquals(24, draft.cursorPosition)
    }

    @Test
    fun correctionCleansWholeActiveEditableFieldWhenNoSelectionExists() {
        val draft = TextCorrectionDraft.from(
            TextCorrectionRequest(
                focusedField = editableField(),
                existingText = "  HELLO   WORLD  ",
                selectionStart = 7,
                selectionEnd = 7,
            ),
        )

        assertTrue(draft.canCorrect)
        assertEquals("Hello world.", draft.textToSet)
        assertEquals(12, draft.cursorPosition)
    }

    @Test
    fun correctionDoesNotTouchSensitiveFields() {
        val draft = TextCorrectionDraft.from(
            TextCorrectionRequest(
                focusedField = editableField(isPassword = true),
                existingText = "HELLO WORLD",
                selectionStart = 0,
                selectionEnd = 11,
            ),
        )

        assertFalse(draft.canCorrect)
        assertEquals(TextCorrectionBlockReason.SensitiveField, draft.blockReason)
    }

    private fun editableField(isPassword: Boolean = false) = FocusedFieldSnapshot(
        packageName = "com.example.notes",
        className = "android.widget.EditText",
        isFocused = true,
        isEditable = true,
        isPassword = isPassword,
    )
}
