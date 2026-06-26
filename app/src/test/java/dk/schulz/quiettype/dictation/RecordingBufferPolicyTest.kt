package dk.schulz.quiettype.dictation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordingBufferPolicyTest {
    @Test
    fun maxSamplesComesFromSampleRateAndDuration() {
        assertEquals(960_000, RecordingBufferPolicy.maxSamples(sampleRateHz = 16_000, maxDurationSeconds = 60))
    }

    @Test
    fun acceptsChunkWhenItFitsWithinLimit() {
        assertTrue(
            RecordingBufferPolicy.shouldAcceptMoreSamples(
                currentSamples = 10,
                nextSamples = 5,
                maxSamples = 20,
            ),
        )
    }

    @Test
    fun rejectsChunkAfterLimitIsReached() {
        assertFalse(
            RecordingBufferPolicy.shouldAcceptMoreSamples(
                currentSamples = 20,
                nextSamples = 1,
                maxSamples = 20,
            ),
        )
    }

    @Test
    fun trimsFinalChunkToRemainingCapacity() {
        assertEquals(
            3,
            RecordingBufferPolicy.samplesToKeep(
                currentSamples = 17,
                nextSamples = 8,
                maxSamples = 20,
            ),
        )
    }
}
