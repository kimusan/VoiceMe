package dk.schulz.quiettype.dictation

import com.k2fsa.sherpa.onnx.FeatureConfig
import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineNemoEncDecCtcModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineTransducerModelConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig
import com.k2fsa.sherpa.onnx.OnlineModelConfig
import com.k2fsa.sherpa.onnx.OnlineRecognizerConfig
import com.k2fsa.sherpa.onnx.OnlineTransducerModelConfig
import dk.schulz.quiettype.models.ModelRuntimeKind
import dk.schulz.quiettype.models.VoiceModel
import dk.schulz.quiettype.settings.WhisperPreferredLanguage
import java.io.File

object SherpaRuntimeConfig {
    const val SampleRateHz = 16_000
    private const val FeatureDim = 80
    private const val Provider = "cpu"

    fun canRunDictation(model: VoiceModel, runtimeDirectory: File): Boolean =
        model.runtime.kind in setOf(
            ModelRuntimeKind.SherpaOnnxOfflineTransducer,
            ModelRuntimeKind.SherpaOnnxOfflineCtc,
            ModelRuntimeKind.SherpaOnnxOfflineWhisper,
            ModelRuntimeKind.SherpaOnnxStreamingTransducer,
        ) && model.runtime.requiredFiles.all { runtimeDirectory.resolve(it).isUsableRuntimeFile() }

    fun canRunOnline(model: VoiceModel, runtimeDirectory: File): Boolean =
        model.runtime.kind == ModelRuntimeKind.SherpaOnnxStreamingTransducer &&
            model.runtime.requiredFiles.all { runtimeDirectory.resolve(it).isUsableRuntimeFile() }

    fun buildOfflineRecognizerConfig(
        model: VoiceModel,
        runtimeDirectory: File,
        preferredWhisperLanguage: WhisperPreferredLanguage = WhisperPreferredLanguage.Automatic,
        numThreads: Int = Runtime.getRuntime().availableProcessors().coerceIn(1, 2),
    ): OfflineRecognizerConfig {
        require(model.runtime.kind in setOf(ModelRuntimeKind.SherpaOnnxOfflineTransducer, ModelRuntimeKind.SherpaOnnxOfflineCtc, ModelRuntimeKind.SherpaOnnxOfflineWhisper)) {
            "Model ${model.id} is not an offline sherpa-onnx dictation model"
        }
        require(model.runtime.requiredFiles.all { runtimeDirectory.resolve(it).isUsableRuntimeFile() }) {
            "Model ${model.id} is not prepared for offline sherpa-onnx dictation"
        }
        val tokensFile = model.requiredRuntimeFile(exact = "tokens.txt", containing = "tokens")
        val offlineModelConfig = when (model.runtime.kind) {
            ModelRuntimeKind.SherpaOnnxOfflineTransducer -> {
                val encoderFile = model.requiredRuntimeFile(containing = "encoder")
                val decoderFile = model.requiredRuntimeFile(containing = "decoder")
                val joinerFile = model.requiredRuntimeFile(containing = "joiner")
                OfflineModelConfig(
                    transducer = OfflineTransducerModelConfig(
                        encoder = runtimeDirectory.resolve(encoderFile).absolutePath,
                        decoder = runtimeDirectory.resolve(decoderFile).absolutePath,
                        joiner = runtimeDirectory.resolve(joinerFile).absolutePath,
                    ),
                    tokens = runtimeDirectory.resolve(tokensFile).absolutePath,
                    numThreads = numThreads,
                    provider = Provider,
                    debug = false,
                )
            }
            ModelRuntimeKind.SherpaOnnxOfflineCtc -> {
                val modelFile = model.requiredRuntimeFile(containing = "model")
                OfflineModelConfig(
                    nemo = OfflineNemoEncDecCtcModelConfig(
                        model = runtimeDirectory.resolve(modelFile).absolutePath,
                    ),
                    tokens = runtimeDirectory.resolve(tokensFile).absolutePath,
                    numThreads = numThreads,
                    provider = Provider,
                    debug = false,
                )
            }
            ModelRuntimeKind.SherpaOnnxOfflineWhisper -> {
                val encoderFile = model.requiredRuntimeFile(containing = "encoder")
                val decoderFile = model.requiredRuntimeFile(containing = "decoder")
                OfflineModelConfig(
                    whisper = OfflineWhisperModelConfig(
                        encoder = runtimeDirectory.resolve(encoderFile).absolutePath,
                        decoder = runtimeDirectory.resolve(decoderFile).absolutePath,
                        language = preferredWhisperLanguage.whisperCode.ifBlank { "auto" },
                        task = "transcribe",
                    ),
                    tokens = runtimeDirectory.resolve(tokensFile).absolutePath,
                    numThreads = numThreads,
                    provider = Provider,
                    debug = false,
                )
            }
            else -> error("Unsupported offline sherpa model ${model.id}")
        }

        return OfflineRecognizerConfig(
            featConfig = FeatureConfig(
                sampleRate = SampleRateHz,
                featureDim = FeatureDim,
                dither = 0.0f,
            ),
            modelConfig = offlineModelConfig,
            decodingMethod = "greedy_search",
        )
    }

    fun buildOnlineRecognizerConfig(
        model: VoiceModel,
        runtimeDirectory: File,
        numThreads: Int = Runtime.getRuntime().availableProcessors().coerceIn(1, 4),
    ): OnlineRecognizerConfig {
        require(canRunOnline(model, runtimeDirectory)) {
            "Model ${model.id} is not prepared for online sherpa-onnx dictation"
        }
        val encoderFile = model.requiredRuntimeFile(containing = "encoder")
        val decoderFile = model.requiredRuntimeFile(containing = "decoder")
        val joinerFile = model.requiredRuntimeFile(containing = "joiner")
        val tokensFile = model.requiredRuntimeFile(exact = "tokens.txt", containing = "tokens")

        return OnlineRecognizerConfig(
            featConfig = FeatureConfig(
                sampleRate = SampleRateHz,
                featureDim = FeatureDim,
                dither = 0.0f,
            ),
            modelConfig = OnlineModelConfig(
                transducer = OnlineTransducerModelConfig(
                    encoder = runtimeDirectory.resolve(encoderFile).absolutePath,
                    decoder = runtimeDirectory.resolve(decoderFile).absolutePath,
                    joiner = runtimeDirectory.resolve(joinerFile).absolutePath,
                ),
                tokens = runtimeDirectory.resolve(tokensFile).absolutePath,
                numThreads = numThreads,
                provider = Provider,
                debug = false,
            ),
            enableEndpoint = true,
            decodingMethod = "greedy_search",
        )
    }

    private fun File.isUsableRuntimeFile(): Boolean = isFile && length() > 0L

    private fun VoiceModel.requiredRuntimeFile(
        prefix: String? = null,
        exact: String? = null,
        containing: String? = null,
    ): String = runtime.requiredFiles.firstOrNull { file ->
        (exact != null && file == exact) ||
            (prefix != null && file.startsWith(prefix)) ||
            (containing != null && file.contains(containing))
    } ?: error("Model $id is missing required ${prefix ?: containing ?: exact} runtime file metadata")
}
