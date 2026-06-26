package dk.schulz.quiettype.dictation

object RecordingBufferPolicy {
    const val DefaultMaxOfflineDurationSeconds = 60

    fun maxSamples(
        sampleRateHz: Int = SherpaRuntimeConfig.SampleRateHz,
        maxDurationSeconds: Int = DefaultMaxOfflineDurationSeconds,
    ): Int = sampleRateHz * maxDurationSeconds

    fun shouldAcceptMoreSamples(currentSamples: Int, nextSamples: Int, maxSamples: Int): Boolean =
        currentSamples < maxSamples && nextSamples > 0

    fun samplesToKeep(currentSamples: Int, nextSamples: Int, maxSamples: Int): Int =
        (maxSamples - currentSamples).coerceIn(0, nextSamples)
}
