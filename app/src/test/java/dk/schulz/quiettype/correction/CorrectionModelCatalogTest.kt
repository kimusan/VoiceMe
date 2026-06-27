package dk.schulz.quiettype.correction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CorrectionModelCatalogTest {
    @Test
    fun defaultCorrectionModelCatalogOffersMobileSizedSmolLmOptions() {
        val catalog = CorrectionModelCatalog.default()

        assertEquals("deterministic-cleanup", catalog.defaultModel.id)
        assertTrue(catalog.models.any { it.id == "smollm2-360m-instruct-q4" })
        assertTrue(catalog.models.any { it.id == "smollm2-1.7b-instruct-q4" })
        assertTrue(catalog.models.all { it.sizeMegabytes <= 1200 })
    }
}
