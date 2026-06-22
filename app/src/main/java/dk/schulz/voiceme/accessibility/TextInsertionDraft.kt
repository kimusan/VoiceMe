package dk.schulz.voiceme.accessibility

data class TextInsertionRequest(
    val focusedField: FocusedFieldSnapshot,
    val existingText: String,
    val transcript: String,
)

enum class TextInsertionBlockReason {
    None,
    NotEditable,
    SensitiveField,
    EmptyTranscript,
}

data class TextInsertionDraft(
    val canInsert: Boolean,
    val textToSet: String,
    val blockReason: TextInsertionBlockReason,
) {
    companion object {
        fun from(request: TextInsertionRequest): TextInsertionDraft {
            val transcript = request.transcript.trim()
            return when {
                transcript.isEmpty() -> blocked(TextInsertionBlockReason.EmptyTranscript)
                !request.focusedField.isFocused || !request.focusedField.isEditable ->
                    blocked(TextInsertionBlockReason.NotEditable)
                request.focusedField.isPassword -> blocked(TextInsertionBlockReason.SensitiveField)
                else -> TextInsertionDraft(
                    canInsert = true,
                    textToSet = appendTranscript(request.existingText, transcript),
                    blockReason = TextInsertionBlockReason.None,
                )
            }
        }

        private fun blocked(reason: TextInsertionBlockReason): TextInsertionDraft = TextInsertionDraft(
            canInsert = false,
            textToSet = "",
            blockReason = reason,
        )

        private fun appendTranscript(existingText: String, transcript: String): String {
            val existing = existingText.trimEnd()
            return if (existing.isBlank()) {
                transcript
            } else {
                "$existing $transcript"
            }
        }
    }
}
