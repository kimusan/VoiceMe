package dk.schulz.quiettype.dictation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OfflineLiveTranscriptCoordinatorTest {
    @Test
    fun emitsStableLiveDeltasFromRepeatedOfflineSnapshots() {
        val coordinator = OfflineLiveTranscriptCoordinator()

        assertNull(coordinator.onPartialTranscript("HEJ"))
        assertEquals("Hej", coordinator.onPartialTranscript("HEJ VERDEN"))
        assertNull(coordinator.onPartialTranscript("HEJ VERDEN"))
        assertEquals("verden", coordinator.onPartialTranscript("HEJ VERDEN IGEN"))
    }

    @Test
    fun finalizesWithOnlyRemainingTailAfterLiveCommits() {
        val coordinator = OfflineLiveTranscriptCoordinator()

        assertEquals("Hej", coordinator.onPartialTranscript("HEJ VERDEN"))
        assertEquals("verden igen", coordinator.onFinalTranscript("HEJ VERDEN IGEN"))
        assertNull(coordinator.onFinalTranscript("HEJ VERDEN IGEN"))
    }
}
