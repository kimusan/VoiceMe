package dk.schulz.quiettype.models

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelDownloadPolicyTest {
    @Test
    fun offlineModeAllowsExplicitUserInitiatedModelDownloads() {
        val model = ModelCatalog.default().recommended

        val decision = ModelDownloadPolicy.canDownload(
            model = model,
            offlineOnly = true,
            userInitiated = true,
            activeDownloadId = null,
        )

        assertTrue(decision is ModelDownloadDecision.Allowed)
    }

    @Test
    fun blocksDownloadsThatAreNotUserInitiated() {
        val model = ModelCatalog.default().recommended

        val decision = ModelDownloadPolicy.canDownload(
            model = model,
            offlineOnly = true,
            userInitiated = false,
            activeDownloadId = null,
        )

        assertTrue(decision is ModelDownloadDecision.Blocked)
        decision as ModelDownloadDecision.Blocked
        assertFalse(decision.reason.contains("offline-only", ignoreCase = true))
    }

    @Test
    fun blocksUnsupportedModelsAndParallelDownloads() {
        val unsupported = ModelCatalog.default().recommended.copy(
            isOfflineCapable = false,
            runtime = ModelCatalog.default().recommended.runtime.copy(requiredFiles = emptyList()),
        )

        assertTrue(
            ModelDownloadPolicy.canDownload(
                model = unsupported,
                offlineOnly = true,
                userInitiated = true,
                activeDownloadId = null,
            ) is ModelDownloadDecision.Blocked,
        )
        assertTrue(
            ModelDownloadPolicy.canDownload(
                model = ModelCatalog.default().recommended,
                offlineOnly = true,
                userInitiated = true,
                activeDownloadId = "other-model",
            ) is ModelDownloadDecision.Blocked,
        )
    }
}
