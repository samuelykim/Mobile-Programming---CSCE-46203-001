package com.example.degreeplanner.local

import androidx.room.Entity
import androidx.room.PrimaryKey

//stores course in the local room database
@Entity(tableName = "courses")
data class CourseEntity(

    //identifier ex: CSCE 36103
    @PrimaryKey
    val id: String,

    //name of course
    val name: String,

    //number of credit hours
    val creditHours: Int,

    //description of the course
    val description: String,

    //official prerequisite information
    val prerequisiteText: String?,

    //IDs of prerequisite courses
    val prerequisiteCourseIds: List<String>,

    //if our prerequisite rules are complete
    val prerequisiteLogicComplete: Boolean,

    //typical semesters the course is offered
    val typicalTerms: List<String>,

    //interests associated with the course
    val interestTags: List<String>
)