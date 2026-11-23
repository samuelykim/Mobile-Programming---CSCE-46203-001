package com.example.degreeplanner.data.model

data class DegreeTemplate(
    val degreeId: String,
    val degreeName: String,
    val catalogYear: String,
    val totalCreditHours: Int,
    val coursePlacements: List<CoursePlacement>,
    val electiveSlots: List<ElectiveSlot>
)

data class CoursePlacement(
    val courseId: String,
    val semesterNumber: Int
)

data class ElectiveSlot(
    val requirementId: String,
    val name: String,
    val semesterNumber: Int,
    val creditHours: Int
)
