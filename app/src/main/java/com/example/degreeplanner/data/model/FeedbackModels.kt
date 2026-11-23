package com.example.degreeplanner.data.model

data class CourseRatingMetrics(
    val overallRating: Float = 4.5f,
    val difficultyRating: Float = 3.5f,
    val workloadRating: Float = 3.8f,
    val organizationRating: Float = 4.4f,
    val totalSubmissions: Int = 12
)

data class DetailedCourseFeedback(
    val id: String = "",
    val courseId: String,
    val studentName: String,
    val term: String = "Fall 2025",
    val overallRating: Float,
    val difficultyRating: Float,
    val workloadRating: Float,
    val organizationRating: Float,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)
