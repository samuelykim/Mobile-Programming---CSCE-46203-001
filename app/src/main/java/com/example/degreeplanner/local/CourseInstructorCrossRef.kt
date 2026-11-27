package com.example.degreeplanner.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

//connects instructors to the courses they teach
@Entity(
    tableName = "course_instructors",
    primaryKeys = ["courseId", "instructorId"],
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InstructorEntity::class,
            parentColumns = ["instructorId"],
            childColumns = ["instructorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("instructorId")]
)
data class CourseInstructorCrossRef(
    val courseId: String,
    val instructorId: String
)