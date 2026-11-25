package com.example.degreeplanner.ui.screens.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.degreeplanner.data.model.DetailedCourseFeedback
import com.example.degreeplanner.navigation.Screen
import com.example.degreeplanner.viewmodel.FeedbackViewModel
import kotlin.math.roundToInt

@Composable
fun CourseFeedbackScreen(
    courseId: String = "CSCE 20004",
    viewModel: FeedbackViewModel = viewModel(),
    onNavigateTo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(courseId) {
        viewModel.loadFeedbackForCourse(courseId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Course Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = uiState.courseId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = uiState.courseName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Submissions: ${uiState.metrics.totalSubmissions}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${uiState.metrics.overallRating} / 5.0 ★",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Average Ratings Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Average Rating Breakdown (0 - 5 Scale)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider()

                RatingMetricRow("Overall Experience", uiState.metrics.overallRating)
                RatingMetricRow("Course Difficulty", uiState.metrics.difficultyRating)
                RatingMetricRow("Workload Intensity", uiState.metrics.workloadRating)
                RatingMetricRow("Course Organization", uiState.metrics.organizationRating)
            }
        }

        // Submit / Update Rating Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (uiState.isUserUpdate) "Update Your Submitted Feedback" else "Submit Course Feedback & Rating",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider()

                RatingSliderInput(
                    label = "Overall Experience",
                    value = uiState.overallRatingInput,
                    onValueChange = { viewModel.onOverallRatingChange(it) }
                )

                RatingSliderInput(
                    label = "Difficulty (0 = Easy, 5 = Very Hard)",
                    value = uiState.difficultyRatingInput,
                    onValueChange = { viewModel.onDifficultyRatingChange(it) }
                )

                RatingSliderInput(
                    label = "Workload (0 = Light, 5 = Very Heavy)",
                    value = uiState.workloadRatingInput,
                    onValueChange = { viewModel.onWorkloadRatingChange(it) }
                )

                RatingSliderInput(
                    label = "Organization & Structure",
                    value = uiState.organizationRatingInput,
                    onValueChange = { viewModel.onOrganizationRatingChange(it) }
                )

                Button(
                    onClick = { viewModel.submitFeedback() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (uiState.isUserUpdate) "Update Feedback" else "Submit Feedback",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.isSubmitted) {
                    Text(
                        text = "✓ Feedback saved successfully!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { onNavigateTo(Screen.CourseOverview.createRoute(uiState.courseId)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Course Overview")
        }

        OutlinedButton(
            onClick = { onNavigateTo(Screen.Entry.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Home")
        }
    }
}

@Composable
fun RatingMetricRow(label: String, rating: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "$rating / 5.0 ★",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun RatingSliderInput(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${(Math.round(value * 10) / 10.0f)} ★",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = {
                //only allow whole number ratings from 0 to 5
                onValueChange(it.roundToInt().toFloat())
            },
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReviewCard(review: DetailedCourseFeedback) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${review.studentName} (${review.term})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${review.overallRating} ★",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Difficulty: ${review.difficultyRating}★", style = MaterialTheme.typography.bodySmall)
                Text("Workload: ${review.workloadRating}★", style = MaterialTheme.typography.bodySmall)
                Text("Org: ${review.organizationRating}★", style = MaterialTheme.typography.bodySmall)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

            Text(
                text = "\"${review.comment}\"",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
