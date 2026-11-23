package com.example.degreeplanner.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.degreeplanner.navigation.Screen
import com.example.degreeplanner.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header & Progress Indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Student Questionnaire",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Step ${uiState.currentStep + 1} of ${uiState.totalSteps}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                LinearProgressIndicator(
                    progress = { (uiState.currentStep + 1).toFloat() / uiState.totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        HorizontalDivider()

        // Page Content based on current step
        when (uiState.currentStep) {
            0 -> AttendanceAndDegreePage(
                preferences = uiState.preferences,
                availableDegrees = uiState.availableDegrees,
                onAttendsUarkChange = { viewModel.setAttendsUark(it) },
                onDegreeChange = { degreeId, degreeName -> viewModel.setDegree(degreeId, degreeName) }
            )

            1 -> InterestsPage(
                preferences = uiState.preferences,
                availableInterests = uiState.availableInterests,
                onInterestToggle = { viewModel.toggleInterest(it) }
            )

            2 -> WorkloadPage(
                preferences = uiState.preferences,
                onWorkloadChange = { viewModel.setWorkloadPreference(it) },
                onCreditHoursChange = { viewModel.setTargetCreditHours(it) }
            )

            3 -> ReviewSummaryPage(
                preferences = uiState.preferences,
                onEditStep = { step -> viewModel.goToStep(step) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Footer: Back & Next / Finish Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.currentStep > 0) {
                OutlinedButton(
                    onClick = { viewModel.previousPage() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            Button(
                onClick = {
                    val isFinished = viewModel.nextPage()
                    if (isFinished) {
                        onNavigateTo(Screen.Recommendation.route)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (uiState.currentStep == uiState.totalSteps - 1) "Generate Plan" else "Next"
                )
            }
        }

        OutlinedButton(
            onClick = { onNavigateTo(Screen.Entry.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Home")
        }
    }
}
