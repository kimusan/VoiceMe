package dk.schulz.quiettype.correction

data class CorrectionModel(
    val id: String,
    val name: String,
    val description: String,
    val engine: String,
    val sizeMegabytes: Int,
    val isDeterministic: Boolean = false,
)

data class CorrectionModelCatalog(
    val models: List<CorrectionModel>,
) {
    val defaultModel: CorrectionModel = models.first()

    fun modelById(modelId: String): CorrectionModel? = models.firstOrNull { it.id == modelId }

    companion object {
        fun default(): CorrectionModelCatalog = CorrectionModelCatalog(
            models = listOf(
                CorrectionModel(
                    id = "deterministic-cleanup",
                    name = "Fast local cleanup",
                    description = "No LLM download. Normalizes spacing, casing, and punctuation directly on-device.",
                    engine = "Built-in deterministic policy",
                    sizeMegabytes = 0,
                    isDeterministic = true,
                ),
                CorrectionModel(
                    id = "smollm2-360m-instruct-q4",
                    name = "SmolLM2 360M Instruct q4",
                    description = "Small local correction candidate for grammar cleanup on mobile. Planned via llama.cpp/ExecuTorch-style runtime before release use.",
                    engine = "Small local LLM candidate",
                    sizeMegabytes = 250,
                ),
                CorrectionModel(
                    id = "smollm2-1.7b-instruct-q4",
                    name = "SmolLM2 1.7B Instruct q4",
                    description = "Higher-quality local correction candidate for newer phones. Larger download and slower startup than 360M.",
                    engine = "Small local LLM candidate",
                    sizeMegabytes = 1050,
                ),
            ),
        )
    }
}
