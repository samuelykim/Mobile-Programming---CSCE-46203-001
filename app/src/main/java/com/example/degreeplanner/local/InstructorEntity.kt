package com.example.degreeplanner.local

import androidx.room.Entity
import androidx.room.PrimaryKey

//stores instructor info in Room
@Entity(tableName = "instructors")
data class InstructorEntity(

    //unique id for each instructor
    @PrimaryKey
    val instructorId: String,

    //instructors full name
    val name: String,

    //department they belong to
    val department: String,

    //optional contact information
    val email: String? = null,

    //optional office location
    val office: String? = null
)