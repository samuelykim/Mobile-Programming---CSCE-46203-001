package com.example.degreeplanner.data.model

data class Instructor(
    val id: String,
    val name: String,
    val title: String = "Faculty of Computer Science",
    val email: String = "",
    val taughtCourses: List<String> = emptyList()
)
