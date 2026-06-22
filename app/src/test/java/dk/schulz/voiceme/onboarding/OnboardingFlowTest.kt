package dk.schulz.voiceme.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingFlowTest {
    @Test
    fun defaultFlowStartsAtWelcomeAndRequiresAllStepsBeforeCompletion() {
        val flow = OnboardingFlow.default()

        assertEquals(5, flow.totalSteps)
        assertEquals(OnboardingStepId.Welcome, flow.currentStep(0).id)
        assertFalse(flow.isComplete(0))
        assertFalse(flow.isComplete(flow.lastStepIndex))
        assertTrue(flow.isComplete(flow.totalSteps))
    }

    @Test
    fun nextAndBackClampWithinValidOnboardingRange() {
        val flow = OnboardingFlow.default()

        assertEquals(0, flow.previousIndex(0))
        assertEquals(1, flow.nextIndex(0))
        assertEquals(flow.lastStepIndex, flow.nextIndex(flow.lastStepIndex))
        assertEquals(flow.lastStepIndex - 1, flow.previousIndex(flow.lastStepIndex))
    }

    @Test
    fun defaultFlowIncludesPrivacyPermissionsAndModelSetupMilestones() {
        val ids = OnboardingFlow.default().steps.map { it.id }

        assertEquals(
            listOf(
                OnboardingStepId.Welcome,
                OnboardingStepId.Privacy,
                OnboardingStepId.InteractionMode,
                OnboardingStepId.Microphone,
                OnboardingStepId.OfflineModel,
            ),
            ids,
        )
    }
}
