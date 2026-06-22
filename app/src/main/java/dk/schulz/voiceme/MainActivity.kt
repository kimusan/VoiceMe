package dk.schulz.voiceme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import dk.schulz.voiceme.dictation.DictationSessionReducer
import dk.schulz.voiceme.dictation.DictationSessionState
import dk.schulz.voiceme.dictation.VoiceMeRecordingService
import dk.schulz.voiceme.models.ModelCatalogReducer
import dk.schulz.voiceme.models.ModelCatalogState
import dk.schulz.voiceme.settings.AppSettings
import dk.schulz.voiceme.settings.AppSettingsStore
import dk.schulz.voiceme.ui.VoiceMeApp

class MainActivity : ComponentActivity() {
    private var appSettings by mutableStateOf(AppSettings.default())
    private var dictationState by mutableStateOf(
        DictationSessionState.idle(hasMicrophonePermission = false),
    )

    private val microphonePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        dictationState = dictationState.copy(hasMicrophonePermission = granted)
        if (granted) {
            startRecordingShell()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsStore = AppSettingsStore(this)
        appSettings = settingsStore.load()
        dictationState = DictationSessionState.idle(hasMicrophonePermission = hasMicrophonePermission())

        fun saveSettings(settings: AppSettings) {
            appSettings = settings
            settingsStore.save(settings)
        }

        fun modelCatalogState(): ModelCatalogState = ModelCatalogState(
            selectedModelId = appSettings.selectedModelId,
            downloadedModelIds = appSettings.downloadedModelIds,
        )

        setContent {
            VoiceMeApp(
                appSettings = appSettings,
                dictationState = dictationState,
                modelCatalogState = modelCatalogState(),
                onSettingsChange = ::saveSettings,
                onOpenAccessibilitySettings = {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                },
                onRequestMicrophonePermission = {
                    microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                onStartRecordingShell = {
                    if (hasMicrophonePermission()) {
                        startRecordingShell()
                    } else {
                        microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onStopRecordingShell = ::stopRecordingShell,
                onSelectModel = { modelId ->
                    saveSettings(appSettings.copy(selectedModelId = modelId))
                },
                onDownloadModel = { modelId ->
                    val updated = ModelCatalogReducer.markDownloaded(modelCatalogState(), modelId)
                    saveSettings(
                        appSettings.copy(
                            selectedModelId = updated.selectedModelId,
                            downloadedModelIds = updated.downloadedModelIds,
                        ),
                    )
                },
                onDeleteModel = { modelId ->
                    val updated = ModelCatalogReducer.deleteModel(modelCatalogState(), modelId)
                    saveSettings(appSettings.copy(downloadedModelIds = updated.downloadedModelIds))
                },
            )
        }
    }

    private fun startRecordingShell() {
        dictationState = DictationSessionReducer.startRecording(
            dictationState.copy(hasMicrophonePermission = hasMicrophonePermission()),
        )
        if (dictationState.isRecording) {
            ContextCompat.startForegroundService(
                this,
                VoiceMeRecordingService.startIntent(this),
            )
        }
    }

    private fun stopRecordingShell() {
        stopService(VoiceMeRecordingService.stopIntent(this))
        dictationState = DictationSessionReducer.stopRecording(dictationState)
    }

    private fun hasMicrophonePermission(): Boolean = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO,
    ) == PackageManager.PERMISSION_GRANTED
}
