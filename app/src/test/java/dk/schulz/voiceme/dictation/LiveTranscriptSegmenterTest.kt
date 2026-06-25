package dk.schulz.voiceme.dictation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LiveTranscriptSegmenterTest {
    @Test
    fun emitsCompletedSentencesOnlyOnce() {
        val segmenter = LiveTranscriptSegmenter()

        assertNull(segmenter.nextCompletedSegment("hello wor"))
        assertEquals("hello world.", segmenter.nextCompletedSegment("hello world. this is"))
        assertNull(segmenter.nextCompletedSegment("hello world. this is"))
        assertEquals("this is live.", segmenter.nextCompletedSegment("hello world. this is live."))
    }

    @Test
    fun remainingSegmentReturnsUncommittedTailAtStop() {
        val segmenter = LiveTranscriptSegmenter()

        assertEquals("first sentence.", segmenter.nextCompletedSegment("first sentence. unfinished tail"))
        assertEquals("unfinished tail", segmenter.remainingSegment("first sentence. unfinished tail"))
        assertNull(segmenter.remainingSegment("first sentence. unfinished tail"))
    }
}
