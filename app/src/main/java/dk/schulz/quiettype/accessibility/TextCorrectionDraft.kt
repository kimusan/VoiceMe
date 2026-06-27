package dk.schulz.quiettype.accessibility

import dk.schulz.quiettype.correction.QuickCorrectionPolicy

data class TextCorrectionRequest(
    val focusedField: FocusedFieldSnapshot,
    val existingText: String,
    val hintText: String? = null,
    val selectionStart: Int? = null,
    val selectionEnd: Int? = null,
)

enum class TextCorrectionBlockReason {
    None,
    NotEditable,
    SensitiveField,
    EmptyText,
    NoChange,
}

data class TextCorrectionDraft(
    val canCorrect: Boolean,
    val textToSet: String,
    val blockReason: TextCorrectionBlockReason,
    val cursorPosition: Int? = null,
) {
    companion object {
        fun from(request: TextCorrectionRequest): TextCorrectionDraft {
            if (!request.focusedField.isFocused || !request.focusedField.isEditable) {
                return blocked(TextCorrectionBlockReason.NotEditable)
            }
            if (FocusedFieldSensitivity.isSensitive(request.focusedField)) {
                return blocked(TextCorrectionBlockReason.SensitiveField)
            }

            val hint = request.hintText?.trim()
            val fieldLooksEmpty = hint != null && request.existingText.trim() == hint
            val baseText = if (fieldLooksEmpty) "" else request.existingText
            if (baseText.isBlank()) return blocked(TextCorrectionBlockReason.EmptyText)

            val range = normalizedRange(
                selectionStart = request.selectionStart,
                selectionEnd = request.selectionEnd,
                textLength = baseText.length,
            )
            val selectedRange = range?.takeIf { it.first != it.second }
            val (textToCorrect, replacementRange) = if (selectedRange != null) {
                baseText.substring(selectedRange.first, selectedRange.second) to selectedRange
            } else {
                baseText to (0 to baseText.length)
            }

            val corrected = QuickCorrectionPolicy.autoCorrect(textToCorrect)
            if (corrected == textToCorrect) return blocked(TextCorrectionBlockReason.NoChange)

            val (start, end) = replacementRange
            val replacementText = baseText.substring(0, start) + corrected + baseText.substring(end)
            return TextCorrectionDraft(
                canCorrect = true,
                textToSet = replacementText,
                blockReason = TextCorrectionBlockReason.None,
                cursorPosition = start + corrected.length,
            )
        }

        private fun blocked(reason: TextCorrectionBlockReason): TextCorrectionDraft = TextCorrectionDraft(
            canCorrect = false,
            textToSet = "",
            blockReason = reason,
        )

        private fun normalizedRange(selectionStart: Int?, selectionEnd: Int?, textLength: Int): Pair<Int, Int>? {
            if (selectionStart == null || selectionEnd == null) return null
            if (selectionStart < 0 || selectionEnd < 0) return null
            val start = minOf(selectionStart, selectionEnd).coerceAtMost(textLength)
            val end = maxOf(selectionStart, selectionEnd).coerceAtMost(textLength)
            return start to end
        }
    }
}

