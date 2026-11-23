package com.example.degreeplanner.data.model

data class SemesterPlan(
    val semesterNumber: Int,
    val courses: List<Course>,
    val electives: List<ElectiveSlot>,
    val totalCredits: Int
)

data class DegreePlan(
    val degreeName: String,
    val semesters: List<SemesterPlan>,
    val totalCredits: Int,
    val warnings: List<String>
)
