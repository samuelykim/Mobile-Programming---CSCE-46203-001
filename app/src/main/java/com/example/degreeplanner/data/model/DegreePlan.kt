package com.example.degreeplanner.data.model

data class DegreePlan(
    val degreeName: String,
    val semesters: List<SemesterPlan> = emptyList(),
    val totalCredits: Int = 0,
    val warnings: List<String> = emptyList()
)
