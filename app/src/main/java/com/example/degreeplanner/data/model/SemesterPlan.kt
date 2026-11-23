package com.example.degreeplanner.data.model

data class SemesterCourseItem(
    val courseId: String,
    val courseName: String,
    val creditHours: Int,
    val isElective: Boolean = false,
    val description: String = ""
)

data class SemesterPlan(
    val semesterNumber: Int,
    val semesterName: String,
    val courses: List<SemesterCourseItem>,
    val totalCreditHours: Int
)

data class FullDegreePlan(
    val degreeId: String,
    val degreeName: String,
    val totalDegreeCredits: Int,
    val semesters: List<SemesterPlan>,
    val recommendationExplanation: String
)
