package dk.schulz.quiettype.correction

enum class QuickCorrectionAction(val label: String) {
    NormalizeSpacing("Clean spacing"),
    CapitalizeFirst("Capitalize first"),
    SentenceCase("Sentence case"),
    AddPeriod("Add period"),
}

object QuickCorrectionPolicy {
    fun suggestedActions(text: String): List<QuickCorrectionAction> = listOf(
        QuickCorrectionAction.NormalizeSpacing,
        QuickCorrectionAction.CapitalizeFirst,
        QuickCorrectionAction.SentenceCase,
        QuickCorrectionAction.AddPeriod,
    ).filter { action -> apply(text, action) != text }
        .ifEmpty { listOf(QuickCorrectionAction.NormalizeSpacing, QuickCorrectionAction.AddPeriod) }

    fun apply(text: String, action: QuickCorrectionAction): String = when (action) {
        QuickCorrectionAction.NormalizeSpacing -> normalizeSpacing(text)
        QuickCorrectionAction.CapitalizeFirst -> capitalizeFirst(text)
        QuickCorrectionAction.SentenceCase -> sentenceCase(text)
        QuickCorrectionAction.AddPeriod -> addPeriod(text)
    }

    fun autoCorrect(text: String): String = addPeriod(sentenceCase(normalizeSpacing(text)))

    private fun normalizeSpacing(text: String): String = text.trim().replace(Regex("\\s+"), " ")

    private fun capitalizeFirst(text: String): String {
        val normalized = text.trimStart()
        val firstLetterIndex = normalized.indexOfFirst { it.isLetter() }
        if (firstLetterIndex < 0) return normalized
        return normalized.replaceRange(
            firstLetterIndex,
            firstLetterIndex + 1,
            normalized[firstLetterIndex].uppercaseChar().toString(),
        )
    }

    private fun sentenceCase(text: String): String {
        val lower = normalizeSpacing(text).lowercase()
        return capitalizeFirst(lower)
    }

    private fun addPeriod(text: String): String {
        val normalized = text.trim()
        if (normalized.isEmpty()) return normalized
        return if (normalized.last() in setOf('.', '!', '?')) normalized else "$normalized."
    }
}
