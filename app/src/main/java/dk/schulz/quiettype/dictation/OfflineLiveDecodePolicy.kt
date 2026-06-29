package dk.schulz.quiettype.dictation

class OfflineLiveDecodePolicy(
    private val firstDecodeSamples: Int,
    private val subsequentDecodeIntervalSamples: Int,
) {
    private var nextDecodeAtSamples: Int = firstDecodeSamples

    init {
        require(firstDecodeSamples > 0) { "firstDecodeSamples must be > 0" }
        require(subsequentDecodeIntervalSamples > 0) { "subsequentDecodeIntervalSamples must be > 0" }
    }

    fun onSamplesCaptured(totalSamples: Int): Boolean {
        if (totalSamples < nextDecodeAtSamples) return false
        nextDecodeAtSamples = totalSamples + subsequentDecodeIntervalSamples
        return true
    }
}
