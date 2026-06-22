package dk.schulz.voiceme.onboarding

enum class OnboardingStepId {
    Welcome,
    Privacy,
    InteractionMode,
    Microphone,
    OfflineModel,
}

data class OnboardingStep(
    val id: OnboardingStepId,
    val eyebrow: String,
    val title: String,
    val body: String,
    val primaryAction: String = "Continue",
)

data class OnboardingFlow(
    val steps: List<OnboardingStep>,
) {
    init {
        require(steps.isNotEmpty()) { "Onboarding flow must contain at least one step." }
    }

    val totalSteps: Int = steps.size
    val lastStepIndex: Int = steps.lastIndex

    fun currentStep(index: Int): OnboardingStep = steps[index.coerceIn(0, lastStepIndex)]

    fun nextIndex(index: Int): Int = (index + 1).coerceAtMost(lastStepIndex)

    fun previousIndex(index: Int): Int = (index - 1).coerceAtLeast(0)

    fun isComplete(index: Int): Boolean = index >= totalSteps

    companion object {
        fun default(): OnboardingFlow = OnboardingFlow(
            steps = listOf(
                OnboardingStep(
                    id = OnboardingStepId.Welcome,
                    eyebrow = "Step 1 of 5",
                    title = "Private voice dictation for any text field.",
                    body = "VoiceMe will appear only when you need it, next to your normal keyboard, so typing still works as usual.",
                    primaryAction = "Start setup",
                ),
                OnboardingStep(
                    id = OnboardingStepId.Privacy,
                    eyebrow = "Step 2 of 5",
                    title = "Your voice stays on this phone.",
                    body = "VoiceMe is designed for offline speech recognition, no telemetry, and no transcript history by default.",
                ),
                OnboardingStep(
                    id = OnboardingStepId.InteractionMode,
                    eyebrow = "Step 3 of 5",
                    title = "Choose how VoiceMe appears.",
                    body = "The recommended mode is a small floating mic button shown beside the keyboard. A VoiceMe keyboard fallback can be added later for stricter apps.",
                ),
                OnboardingStep(
                    id = OnboardingStepId.Microphone,
                    eyebrow = "Step 4 of 5",
                    title = "Microphone permission comes later.",
                    body = "VoiceMe will ask for microphone access only when recording is implemented and you explicitly start the permission step.",
                ),
                OnboardingStep(
                    id = OnboardingStepId.OfflineModel,
                    eyebrow = "Step 5 of 5",
                    title = "Offline model setup will be explicit.",
                    body = "Before downloading a speech model, VoiceMe will show model size, language, license, and checksum so you know exactly what is stored locally.",
                    primaryAction = "Finish for now",
                ),
            ),
        )
    }
}
