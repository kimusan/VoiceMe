package dk.schulz.voiceme.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class VoiceMeAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Text-field detection and floating mic overlay are implemented in later milestones.
    }

    override fun onInterrupt() {
        // No active recording or overlay exists yet, so there is nothing to tear down.
    }
}
