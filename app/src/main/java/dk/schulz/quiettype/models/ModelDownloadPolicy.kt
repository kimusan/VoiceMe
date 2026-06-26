package dk.schulz.quiettype.models

sealed class ModelDownloadDecision {
    data object Allowed : ModelDownloadDecision()
    data class Blocked(val reason: String) : ModelDownloadDecision()
}

object ModelDownloadPolicy {
    fun canDownload(
        model: VoiceModel,
        offlineOnly: Boolean,
        userInitiated: Boolean,
        activeDownloadId: String?,
    ): ModelDownloadDecision {
        if (activeDownloadId != null) {
            return ModelDownloadDecision.Blocked("A model download is already running. Wait for it to finish before starting another.")
        }
        if (!userInitiated) {
            return ModelDownloadDecision.Blocked("Model downloads must be started by an explicit user action.")
        }
        if (!model.isOfflineCapable || model.runtime.requiredFiles.isEmpty()) {
            return ModelDownloadDecision.Blocked("${model.name} is a benchmark/reference entry and is not downloadable in the app yet. Choose a mobile-ready model instead.")
        }
        // offlineOnly means dictation must stay local. It does not block explicit HTTPS model downloads.
        return ModelDownloadDecision.Allowed
    }
}
