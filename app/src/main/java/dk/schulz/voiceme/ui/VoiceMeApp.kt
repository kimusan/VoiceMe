package dk.schulz.voiceme.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.schulz.voiceme.R
import dk.schulz.voiceme.onboarding.OnboardingFlow
import dk.schulz.voiceme.onboarding.OnboardingStep
import dk.schulz.voiceme.ui.theme.VoiceMeTheme

@Composable
fun VoiceMeApp() {
    VoiceMeTheme {
        VoiceMeHomeScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceMeHomeScreen(modifier: Modifier = Modifier) {
    val onboardingFlow = remember { OnboardingFlow.default() }
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    var selectedSection by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SectionChips(
                selectedSection = selectedSection,
                onSelectedSectionChange = { selectedSection = it },
            )
            when (selectedSection) {
                0 -> VoiceMeOnboardingScreen(
                    step = onboardingFlow.currentStep(currentStep),
                    currentStep = currentStep,
                    totalSteps = onboardingFlow.totalSteps,
                    onBack = {
                        currentStep = onboardingFlow.previousIndex(currentStep)
                    },
                    onNext = {
                        if (currentStep == onboardingFlow.lastStepIndex) {
                            selectedSection = 1
                        } else {
                            currentStep = onboardingFlow.nextIndex(currentStep)
                        }
                    },
                )

                1 -> VoiceMeStatusScreen(
                    onReviewSetup = {
                        currentStep = 0
                        selectedSection = 0
                    },
                )

                else -> VoiceMeSettingsPreview()
            }
        }
    }
}

@Composable
private fun SectionChips(
    selectedSection: Int,
    onSelectedSectionChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf("Setup", "Status", "Settings").forEachIndexed { index, label ->
            FilterChip(
                selected = selectedSection == index,
                onClick = { onSelectedSectionChange(index) },
                label = { Text(label) },
            )
        }
    }
}

@Composable
private fun VoiceMeOnboardingScreen(
    step: OnboardingStep,
    currentStep: Int,
    totalSteps: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LinearProgressIndicator(
            progress = { (currentStep + 1).toFloat() / totalSteps.toFloat() },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = step.eyebrow,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = step.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = step.body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Text(
                text = "No permissions are requested by this preview screen. VoiceMe will ask just-in-time once the real microphone, overlay, and model steps are implemented.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onBack,
                enabled = currentStep > 0,
                modifier = Modifier.weight(1f),
            ) {
                Text("Back")
            }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
            ) {
                Text(step.primaryAction)
            }
        }
    }
}

@Composable
private fun VoiceMeStatusScreen(
    onReviewSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "VoiceMe setup preview",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "The app shell is installed and interactive. Dictation, permissions, model downloads, and the floating mic service are the next implementation milestones.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        StatusCard(
            title = "Privacy default",
            body = "Offline-first, no telemetry, no transcript history by default.",
        )
        StatusCard(
            title = "Input strategy",
            body = "Primary path: a floating mic beside the normal keyboard. Fallback path: VoiceMe keyboard/IME for apps that block accessibility insertion.",
        )
        StatusCard(
            title = "Model setup",
            body = "A future model catalog will show size, language, license, and checksum before any download.",
        )
        OutlinedButton(onClick = onReviewSetup) {
            Text("Review setup again")
        }
    }
}

@Composable
private fun VoiceMeSettingsPreview(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Settings preview",
            style = MaterialTheme.typography.headlineMedium,
        )
        StatusCard(
            title = "Dictation interaction",
            body = "Planned options: hold-to-talk, tap-to-toggle, haptics, and endpointing delay.",
        )
        StatusCard(
            title = "Floating control",
            body = "Planned options: size, opacity, position reset, and keyboard-area placement.",
        )
        StatusCard(
            title = "Local data",
            body = "Planned controls: delete models, clear temporary transcripts, and confirm offline-only behavior.",
        )
        StatusCard(
            title = "Sensitive fields",
            body = "Planned behavior: pause or hide dictation for password and other sensitive input fields.",
        )
    }
}

@Composable
private fun StatusCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VoiceMeHomeScreenPreview() {
    VoiceMeTheme(dynamicColor = false) {
        VoiceMeHomeScreen()
    }
}
