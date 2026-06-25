package dk.schulz.voiceme.dictation

class LiveTranscriptSegmenter {
    private var committedLength: Int = 0

    fun nextCompletedSegment(transcript: String): String? {
        val clean = transcript.trim()
        if (clean.length <= committedLength) return null
        val completedEnd = completedSentenceEnd(clean, start = committedLength)
            ?: return null
        val segment = clean.substring(committedLength, completedEnd).trim()
        committedLength = completedEnd
        return segment.takeIf { it.isNotBlank() }
    }

    fun remainingSegment(transcript: String): String? {
        val clean = transcript.trim()
        if (clean.length <= committedLength) return null
        val segment = clean.substring(committedLength).trim()
        committedLength = clean.length
        return segment.takeIf { it.isNotBlank() }
    }

    private fun completedSentenceEnd(text: String, start: Int): Int? {
        for (index in start until text.length) {
            if (text[index].isSentenceTerminal()) {
                return index + 1
            }
        }
        return null
    }

    private fun Char.isSentenceTerminal(): Boolean = this in setOf('.', '!', '?', '。', '！', '？')
}
