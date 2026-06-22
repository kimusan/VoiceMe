package dk.schulz.voiceme.dictation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dk.schulz.voiceme.R

class VoiceMeRecordingService : Service() {
    private var recorder: AudioRecord? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ActionStop) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (!hasMicrophonePermission()) {
            stopSelf()
            return START_NOT_STICKY
        }

        ensureNotificationChannel()
        startForeground(NotificationId, buildNotification())
        startAudioRecordShell()
        return START_STICKY
    }

    override fun onDestroy() {
        stopAudioRecordShell()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("MissingPermission")
    private fun startAudioRecordShell() {
        if (recorder != null || !hasMicrophonePermission()) return
        val minBufferSize = AudioRecord.getMinBufferSize(
            SampleRateHz,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        ).coerceAtLeast(SampleRateHz / 10)

        val candidate = try {
            AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SampleRateHz,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
            )
        } catch (_: SecurityException) {
            stopSelf()
            return
        }
        if (candidate.state == AudioRecord.STATE_INITIALIZED) {
            candidate.startRecording()
            recorder = candidate
        } else {
            candidate.release()
        }
    }

    private fun stopAudioRecordShell() {
        recorder?.let { audioRecord ->
            runCatching { audioRecord.stop() }
            audioRecord.release()
        }
        recorder = null
    }

    private fun hasMicrophonePermission(): Boolean = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO,
    ) == PackageManager.PERMISSION_GRANTED

    private fun buildNotification(): Notification = NotificationCompat.Builder(this, ChannelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("VoiceMe listening preview")
        .setContentText("Microphone shell is active locally; ASR is not connected yet.")
        .setOngoing(true)
        .setSilent(true)
        .build()

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        val existing = manager.getNotificationChannel(ChannelId)
        if (existing == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    ChannelId,
                    "VoiceMe dictation",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    description = "Shows when VoiceMe microphone capture is active."
                },
            )
        }
    }

    companion object {
        private const val ChannelId = "voiceme_dictation"
        private const val NotificationId = 1001
        private const val SampleRateHz = 16_000
        private const val ActionStop = "dk.schulz.voiceme.action.STOP_RECORDING"

        fun startIntent(context: Context): Intent = Intent(context, VoiceMeRecordingService::class.java)
        fun stopIntent(context: Context): Intent = Intent(context, VoiceMeRecordingService::class.java).apply {
            action = ActionStop
        }
    }
}
