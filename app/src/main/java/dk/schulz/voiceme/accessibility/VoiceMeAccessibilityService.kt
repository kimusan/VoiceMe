package dk.schulz.voiceme.accessibility

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import android.widget.Toast
import dk.schulz.voiceme.settings.AppSettingsStore

class VoiceMeAccessibilityService : AccessibilityService() {
    private lateinit var settingsStore: AppSettingsStore
    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private var lastDetection: FocusedFieldDetection? = null

    override fun onCreate() {
        super.onCreate()
        settingsStore = AppSettingsStore(this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || !event.isFocusRelevant()) {
            return
        }

        val node = event.source
        if (node == null) {
            hideOverlay()
            lastDetection = null
            return
        }

        try {
            val snapshot = node.toFocusedFieldSnapshot(
                eventPackageName = event.packageName?.toString(),
                eventClassName = event.className?.toString(),
            )
            val detection = FocusedFieldDetector.detect(
                snapshot = snapshot,
                settings = settingsStore.load(),
            )
            lastDetection = detection

            if (detection.shouldShowOverlay) {
                showOrUpdateOverlay(detection)
            } else {
                hideOverlay()
            }
        } finally {
            node.recycle()
        }
    }

    override fun onInterrupt() {
        hideOverlay()
    }

    override fun onDestroy() {
        hideOverlay()
        super.onDestroy()
    }

    private fun showOrUpdateOverlay(detection: FocusedFieldDetection) {
        val existing = overlayView as? TextView
        if (existing != null) {
            existing.text = overlayLabel(detection)
            return
        }

        val view = TextView(this).apply {
            text = overlayLabel(detection)
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFF6750A4.toInt())
            setPadding(28, 18, 28, 18)
            elevation = 12f
            contentDescription = "VoiceMe dictation preview button"
            setOnClickListener {
                insertStubTranscriptIntoFocusedField()
            }
            setOnTouchListener(OverlayDragTouchListener(windowManager))
        }

        windowManager.addView(view, overlayLayoutParams())
        overlayView = view
    }

    private fun hideOverlay() {
        overlayView?.let { view ->
            runCatching { windowManager.removeView(view) }
        }
        overlayView = null
    }

    private fun insertStubTranscriptIntoFocusedField() {
        val focusedNode = rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        if (focusedNode == null) {
            showToast("No editable text field is focused.")
            return
        }

        try {
            val snapshot = focusedNode.toFocusedFieldSnapshot(
                eventPackageName = focusedNode.packageName?.toString(),
                eventClassName = focusedNode.className?.toString(),
            )
            val draft = TextInsertionDraft.from(
                TextInsertionRequest(
                    focusedField = snapshot,
                    existingText = focusedNode.text?.toString().orEmpty(),
                    transcript = "VoiceMe dictation test",
                ),
            )
            if (!draft.canInsert) {
                showToast("VoiceMe did not insert text: ${draft.blockReason}.")
                return
            }

            val arguments = Bundle().apply {
                putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    draft.textToSet,
                )
            }
            if (focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)) {
                showToast("Inserted VoiceMe test transcript.")
            } else {
                showToast("This field does not accept accessibility text insertion.")
            }
        } finally {
            focusedNode.recycle()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun overlayLabel(detection: FocusedFieldDetection): String {
        val appLabel = detection.packageName?.substringAfterLast('.')?.takeIf { it.isNotBlank() }
        return if (appLabel == null) {
            "🎙 VoiceMe"
        } else {
            "🎙 VoiceMe · $appLabel"
        }
    }

    private fun overlayLayoutParams(): WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT,
    ).apply {
        gravity = Gravity.BOTTOM or Gravity.END
        x = 32
        y = 260
    }

    private fun AccessibilityEvent.isFocusRelevant(): Boolean = eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
        eventType == AccessibilityEvent.TYPE_VIEW_CLICKED ||
        eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
        eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED

    private fun AccessibilityNodeInfo.toFocusedFieldSnapshot(
        eventPackageName: String?,
        eventClassName: String?,
    ): FocusedFieldSnapshot = FocusedFieldSnapshot(
        packageName = packageName?.toString() ?: eventPackageName,
        className = className?.toString() ?: eventClassName,
        isFocused = isFocused,
        isEditable = isEditable,
        isPassword = isPassword,
    )

    private class OverlayDragTouchListener(
        private val windowManager: WindowManager,
    ) : View.OnTouchListener {
        private var startX = 0
        private var startY = 0
        private var downRawX = 0f
        private var downRawY = 0f
        private var dragging = false

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val params = view.layoutParams as? WindowManager.LayoutParams ?: return false
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startX = params.x
                    startY = params.y
                    downRawX = event.rawX
                    downRawY = event.rawY
                    dragging = false
                    return false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - downRawX).toInt()
                    val dy = (event.rawY - downRawY).toInt()
                    if (!dragging && kotlin.math.abs(dx) + kotlin.math.abs(dy) < 12) {
                        return false
                    }
                    dragging = true
                    params.x = (startX - dx).coerceAtLeast(0)
                    params.y = (startY - dy).coerceAtLeast(0)
                    windowManager.updateViewLayout(view, params)
                    return true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val wasDragging = dragging
                    dragging = false
                    return wasDragging
                }
            }
            return false
        }
    }
}
