package com.example.degreeplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeplanner.data.model.CourseRatingMetrics
import com.example.degreeplanner.data.model.DetailedCourseFeedback
import com.example.degreeplanner.data.repository.PlannerRepository
import com.example.degreeplanner.firebase.CourseReviewRepository
import com.example.degreeplanner.firebase.FirebaseAuthManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class CourseFeedbackUiState(
    val courseId: String = "",
    val courseName: String = "",
    val metrics: CourseRatingMetrics = CourseRatingMetrics(
        overallRating = 0f,
        difficultyRating = 0f,
        workloadRating = 0f,
        organizationRating = 0f,
        totalSubmissions = 0
    ),

    // Keep these for compatibility with Cade's screen.
    val reviews: List<DetailedCourseFeedback> = emptyList(),
    val studentNameInput: String = "Current Student",
    val writtenFeedbackInput: String = "",

    val overallRatingInput: Float = 4f,
    val difficultyRatingInput: Float = 3f,
    val workloadRatingInput: Float = 3f,
    val organizationRatingInput: Float = 4f,

    val isSubmitted: Boolean = false,
    val isUserUpdate: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FeedbackViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val plannerRepository = PlannerRepository(application)
    private val firebaseRepository = CourseReviewRepository()

    private val _uiState = MutableStateFlow(CourseFeedbackUiState())
    val uiState: StateFlow<CourseFeedbackUiState> = _uiState.asStateFlow()

    //load ratings directly from firebase.
    fun loadFeedbackForCourse(courseId: String) {
        val courseName = plannerRepository
            .getCourseById(courseId)?.name ?: "Computer Science Course"

        _uiState.value = CourseFeedbackUiState(
            courseId = courseId,
            courseName = courseName,
            isLoading = true
        )

        viewModelScope.launch {
            try {
                FirebaseAuthManager().signInAnonymously()

                val summary = firebaseRepository.getRatingSummary(courseId)
                val ratings = firebaseRepository.getRatingsForCourse(courseId)
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                //check if this student has already rated the course.
                val existingRating = ratings.firstOrNull {
                    it.userId == currentUserId
                }

                _uiState.update { state ->
                    if (state.courseId != courseId) {
                        state
                    } else {
                        state.copy(
                            metrics = CourseRatingMetrics(
                                overallRating = summary.courseAverage.toFloat(),
                                difficultyRating = summary.difficultyAverage.toFloat(),
                                workloadRating = summary.assignmentsAverage.toFloat(),
                                organizationRating = summary.instructorExperienceAverage.toFloat(),
                                totalSubmissions = summary.ratingCount
                            ),
                            overallRatingInput = existingRating?.courseRating?.toFloat() ?: 4f,
                            difficultyRatingInput = existingRating?.difficultyRating?.toFloat() ?: 3f,
                            workloadRatingInput = existingRating?.assignmentsRating?.toFloat() ?: 3f,
                            organizationRatingInput = existingRating?.instructorExperienceRating?.toFloat() ?: 4f,
                            isUserUpdate = existingRating != null,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Could not load ratings."
                    )
                }
            }
        }
    }

    //keep cades existing callback names so ui still works.
    fun onOverallRatingChange(rating: Float) {
        _uiState.update { it.copy(overallRatingInput = rating) }
    }

    fun onDifficultyRatingChange(rating: Float) {
        _uiState.update { it.copy(difficultyRatingInput = rating) }
    }

    fun onWorkloadRatingChange(rating: Float) {
        _uiState.update { it.copy(workloadRatingInput = rating) }
    }

    fun onOrganizationRatingChange(rating: Float) {
        _uiState.update { it.copy(organizationRatingInput = rating) }
    }

    //retained temporarily so older ui references still compile.
    fun onStudentNameChange(name: String) {
        _uiState.update { it.copy(studentNameInput = name) }
    }

    fun onWrittenFeedbackChange(comment: String) {
        _uiState.update { it.copy(writtenFeedbackInput = comment) }
    }

    //save four numerical ratings to firestore.
    fun submitFeedback() {
        val state = _uiState.value
        if (state.courseId.isBlank() || state.isLoading) return

        _uiState.update {
            it.copy(
                isSubmitted = false,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                FirebaseAuthManager().signInAnonymously()

                firebaseRepository.saveRating(
                    courseId = state.courseId,
                    courseRating = state.overallRatingInput.roundToInt(),
                    difficultyRating = state.difficultyRatingInput.roundToInt(),
                    assignmentsRating = state.workloadRatingInput.roundToInt(),
                    instructorExperienceRating = state.organizationRatingInput.roundToInt()
                )

                val summary = firebaseRepository.getRatingSummary(state.courseId)

                _uiState.update {
                    it.copy(
                        metrics = CourseRatingMetrics(
                            overallRating = summary.courseAverage.toFloat(),
                            difficultyRating = summary.difficultyAverage.toFloat(),
                            workloadRating = summary.assignmentsAverage.toFloat(),
                            organizationRating = summary.instructorExperienceAverage.toFloat(),
                            totalSubmissions = summary.ratingCount
                        ),
                        isSubmitted = true,
                        isUserUpdate = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitted = false,
                        isLoading = false,
                        errorMessage = e.message ?: "Could not save rating."
                    )
                }
            }
        }
    }
}
