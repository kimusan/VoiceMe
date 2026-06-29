package dk.schulz.quiettype.dictation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineLiveDecodePolicyTest {
    @Test
    fun waitsForMinimumAudioBeforeFirstDecode() {
        val policy = OfflineLiveDecodePolicy(
            firstDecodeSamples = 6,
            subsequentDecodeIntervalSamples = 4,
        )

        assertFalse(policy.onSamplesCaptured(totalSamples = 3))
        assertFalse(policy.onSamplesCaptured(totalSamples = 5))
        assertTrue(policy.onSamplesCaptured(totalSamples = 6))
    }

    @Test
    fun emitsAdditionalDecodeRequestsOnlyWhenIntervalIsReached() {
        val policy = OfflineLiveDecodePolicy(
            firstDecodeSamples = 6,
            subsequentDecodeIntervalSamples = 4,
        )

        assertTrue(policy.onSamplesCaptured(totalSamples = 6))
        assertFalse(policy.onSamplesCaptured(totalSamples = 7))
        assertFalse(policy.onSamplesCaptured(totalSamples = 9))
        assertTrue(policy.onSamplesCaptured(totalSamples = 10))
        assertFalse(policy.onSamplesCaptured(totalSamples = 12))
        assertTrue(policy.onSamplesCaptured(totalSamples = 14))
    }
}
