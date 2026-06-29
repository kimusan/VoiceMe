package dk.schulz.quiettype.dictation

class OfflineLiveTranscriptCoordinator(
    private val segmenter: LiveTranscriptSegmenter = LiveTranscriptSegmenter(),
) {
    fun onPartialTranscript(transcript: String): String? = segmenter.nextStableSegment(transcript)

    fun onFinalTranscript(transcript: String): String? = segmenter.remainingSegment(transcript)
}
