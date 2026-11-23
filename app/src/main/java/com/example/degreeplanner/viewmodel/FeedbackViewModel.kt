package com.example.degreeplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.degreeplanner.data.model.CourseRatingMetrics
import com.example.degreeplanner.data.model.DetailedCourseFeedback
import com.example.degreeplanner.data.repository.FeedbackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CourseFeedbackUiState(
    val courseId: String = "",
    val courseName: String = "",
    val metrics: CourseRatingMetrics = CourseRatingMetrics(),
    val reviews: List<DetailedCourseFeedback> = emptyList(),
    val studentNameInput: String = "Current Student",
    val overallRatingInput: Float = 4.0f,
    val difficultyRatingInput: Float = 3.0f,
    val workloadRatingInput: Float = 3.0f,
    val organizationRatingInput: Float = 4.0f,
    val writtenFeedbackInput: String = "",
    val isSubmitted: Boolean = false,
    val isUserUpdate: Boolean = false
)

class FeedbackViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FeedbackRepository(application)

    private val _uiState = MutableStateFlow(CourseFeedbackUiState())
    val uiState: StateFlow<CourseFeedbackUiState> = _uiState.asStateFlow()

    fun loadFeedbackForCourse(courseId: String) {
        val courseName = repository.getCourseName(courseId)
        val metrics = repository.getCourseMetrics(courseId)
        val reviews = repository.getReviewsForCourse(courseId)
        val userExistingFeedback = reviews.find { it.studentName == "Current Student" }

        _uiState.update { state ->
            state.copy(
                courseId = courseId,
                courseName = courseName,
                metrics = metrics,
                reviews = reviews,
                studentNameInput = userExistingFeedback?.studentName ?: "Current Student",
                overallRatingInput = userExistingFeedback?.overallRating ?: 4.0f,
                difficultyRatingInput = userExistingFeedback?.difficultyRating ?: 3.0f,
                workloadRatingInput = userExistingFeedback?.workloadRating ?: 3.0f,
                organizationRatingInput = userExistingFeedback?.organizationRating ?: 4.0f,
                writtenFeedbackInput = userExistingFeedback?.comment ?: "",
                isUserUpdate = userExistingFeedback != null
            )
        }
    }

    fun onStudentNameChange(name: String) {
        _uiState.update { it.copy(studentNameInput = name) }
    }

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

    fun onWrittenFeedbackChange(comment: String) {
        _uiState.update { it.copy(writtenFeedbackInput = comment) }
    }

    fun submitFeedback() {
        val state = _uiState.value
        val newFeedback = DetailedCourseFeedback(
            courseId = state.courseId,
            studentName = state.studentNameInput.ifEmpty { "Current Student" },
            term = "Fall 2025",
            overallRating = state.overallRatingInput,
            difficultyRating = state.difficultyRatingInput,
            workloadRating = state.workloadRatingInput,
            organizationRating = state.organizationRatingInput,
            comment = state.writtenFeedbackInput
        )

        repository.saveFeedback(newFeedback)

        val updatedMetrics = repository.getCourseMetrics(state.courseId)
        val updatedReviews = repository.getReviewsForCourse(state.courseId)

        _uiState.update {
            it.copy(
                metrics = updatedMetrics,
                reviews = updatedReviews,
                isSubmitted = true,
                isUserUpdate = true
            )
        }
    }
}
