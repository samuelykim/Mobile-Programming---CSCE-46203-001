package com.example.degreeplanner.data.model

data class CourseFeedbackItem(
    val studentName: String,
    val term: String,
    val rating: Float,
    val comment: String
)

data class Course(
    val id: String,
    val name: String,
    val creditHours: Int,
    val description: String = "",
    val prerequisiteText: String = "",
    val prerequisiteCourseIds: List<String> = emptyList(),
    val prerequisiteLogicComplete: Boolean = false,
    val typicalTerms: List<String> = emptyList(),
    val interestTags: List<String> = emptyList(),
    val instructors: List<String> = emptyList(),
    val previousSchedules: List<String> = emptyList(),
    val averageRating: Float = 4.5f,
    val difficultyRating: Float = 3.5f,
    val studentFeedback: List<CourseFeedbackItem> = emptyList()
)
