package dk.schulz.voiceme.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelCatalogTest {
    @Test
    fun defaultCatalogProvidesCompactOfflineModelFirst() {
        val catalog = ModelCatalog.default()

        assertEquals("sherpa-onnx-streaming-zipformer-en-int8", catalog.recommended.id)
        assertTrue(catalog.recommended.isOfflineCapable)
        assertTrue(catalog.recommended.sizeMegabytes < 100)
    }

    @Test
    fun modelStateRequiresExplicitDownloadBeforeReady() {
        val state = ModelCatalogState.default()

        assertFalse(state.isReadyForDictation)
        assertEquals(ModelInstallState.NotDownloaded, state.selectedInstallState)
    }

    @Test
    fun downloadedSelectedModelMakesCatalogReadyAndDeleteResetsIt() {
        val selected = "sherpa-onnx-streaming-zipformer-en-int8"
        val downloaded = ModelCatalogReducer.markDownloaded(
            state = ModelCatalogState.default().selectModel(selected),
            modelId = selected,
        )

        assertTrue(downloaded.isReadyForDictation)
        assertEquals(ModelInstallState.Downloaded, downloaded.selectedInstallState)

        val deleted = ModelCatalogReducer.deleteModel(downloaded, selected)

        assertFalse(deleted.isReadyForDictation)
        assertEquals(ModelInstallState.NotDownloaded, deleted.selectedInstallState)
    }
}
