package com.example.degreeplanner.ui.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.degreeplanner.navigation.Screen
import com.example.degreeplanner.viewmodel.CourseDetailViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.LaunchedEffect
import com.example.degreeplanner.viewmodel.FeedbackViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CourseOverviewScreen(
    courseId: String = "N/A",
    viewModel: CourseDetailViewModel = viewModel(),
    onNavigateTo: (String) -> Unit
) {
    //observe the selected course from room database
    val courseFlow = remember(viewModel, courseId) {
        viewModel.observeCourse(courseId).map { course ->
            true to course
        }
    }

    val loadResult by courseFlow.collectAsState(
        initial = false to null
    )

    val (isLoaded, course) = loadResult
    //use existing firebase feedback ViewModel
    val ratingsViewModel: FeedbackViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel()

    val ratingsState by ratingsViewModel.uiState.collectAsState()

    //load ratings for the selected course
    LaunchedEffect(courseId) {
        ratingsViewModel.loadFeedbackForCourse(courseId)
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isLoaded) {
            Text(
                text = "Loading course...",
                style = MaterialTheme.typography.bodyLarge
            )
        } else if (course == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Course Not Found ($courseId)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = { onNavigateTo(Screen.Catalog.createRoute(false)) }) {
                        Text("Back to Catalog")
                    }
                }
            }
        } else {
            // Header Card
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = course.id,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = course.name,
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
                            text = "${course.creditHours} Credit Hours",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (course.typicalTerms.isNotEmpty()) {
                            Text(
                                text = "Terms: ${course.typicalTerms.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (course.interestTags.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            course.interestTags.forEach { tag ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
            }

            // Description Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Course Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider()
                    Text(
                        text = course.description.ifEmpty { "No detailed description provided." },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Prerequisites Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Prerequisites",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider()
                    Text(
                        text = course.prerequisiteText ?: "No prerequisite text listed.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (course.prerequisiteCourseIds.isNotEmpty()) {
                        Text(
                            text = "Required prerequisite course IDs: ${course.prerequisiteCourseIds.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Student reported instructor history
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Student-Reported Instructor History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider()

                    if (course.instructors.isEmpty()) {

                        Text(
                            text = "No instructor history available for this course.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    } else {

                        course.instructors.distinct().forEach { instructorName ->

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("• ", fontWeight = FontWeight.Bold)

                                Text(
                                    text = instructorName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Text(
                            text = "Based on student-reported course associations. " +
                                    "Not a confirmed current teaching schedule.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            //student ratings section with firebase
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Student Ratings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider()

                    val metrics = ratingsState.metrics

                    when {
                        ratingsState.isLoading -> {
                            Text("Loading ratings...")
                        }

                        ratingsState.errorMessage != null -> {
                            Text(
                                text = "Could not load ratings: ${ratingsState.errorMessage}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        metrics.totalSubmissions == 0 -> {
                            Text("No ratings submitted yet.")
                        }

                        else -> {
                            Text(
                                "Overall Course: %.1f / 5".format(
                                    metrics.overallRating
                                )
                            )

                            Text(
                                "Difficulty: %.1f / 5".format(
                                    metrics.difficultyRating
                                )
                            )

                            Text(
                                "Assignments: %.1f / 5".format(
                                    metrics.workloadRating
                                )
                            )

                            Text(
                                "Instructor Experience: %.1f / 5".format(
                                    metrics.organizationRating
                                )
                            )

                            Text(
                                "Total Ratings: ${metrics.totalSubmissions}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Button(
                onClick = { onNavigateTo(Screen.Feedback.createRoute(course.id)) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Leave Course Feedback",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = { onNavigateTo(Screen.Catalog.createRoute(false)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Course Catalog")
            }

            OutlinedButton(
                onClick = { onNavigateTo(Screen.Entry.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Home")
            }
        }
    }
}
