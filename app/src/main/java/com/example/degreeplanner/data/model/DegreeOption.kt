package com.example.degreeplanner.data.model

data class DegreeOption(
    val degreeId: String,
    val name: String,
    val planStatus: String = "",
    val totalCreditHours: Int = 120,
    val sourceUrl: String = ""
)
