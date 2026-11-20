package com.example.degreeplanner.data.model

data class Course(
    val id: String,
    val name: String,
    val creditHours: Int,
    val description: String = "",
    val prerequisiteText: String = "",
    val prerequisiteCourseIds: List<String> = emptyList(),
    val prerequisiteLogicComplete: Boolean = false,
    val typicalTerms: List<String> = emptyList(),
    val interestTags: List<String> = emptyList()
)
