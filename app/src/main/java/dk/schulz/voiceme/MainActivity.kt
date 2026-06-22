package dk.schulz.voiceme

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dk.schulz.voiceme.settings.AppSettings
import dk.schulz.voiceme.settings.AppSettingsStore
import dk.schulz.voiceme.ui.VoiceMeApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsStore = AppSettingsStore(this)
        var appSettings by mutableStateOf(settingsStore.load())

        fun saveSettings(settings: AppSettings) {
            appSettings = settings
            settingsStore.save(settings)
        }

        setContent {
            VoiceMeApp(
                appSettings = appSettings,
                onSettingsChange = ::saveSettings,
                onOpenAccessibilitySettings = {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                },
            )
        }
    }
}
