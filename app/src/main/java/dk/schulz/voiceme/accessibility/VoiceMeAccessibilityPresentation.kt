package dk.schulz.voiceme.accessibility

object VoiceMeAccessibilityPresentation {
    const val NotificationTitle = "VoiceMe floating button ready"
    const val NotificationText = "VoiceMe is watching for editable fields locally. Tap a text field to show the mic button."

    fun statusText(isEnabled: Boolean): String = if (isEnabled) {
        "Accessibility service enabled. Tap any editable text field to show the VoiceMe floating mic button."
    } else {
        "Accessibility service not enabled. Open Android Accessibility settings and enable VoiceMe."
    }
}
