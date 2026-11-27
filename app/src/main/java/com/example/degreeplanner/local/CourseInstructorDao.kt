package com.example.degreeplanner.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//handles relationships between courses and instructors
@Dao
interface CourseInstructorDao {

    //connect an instructor to a course
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCourseInstructor(
        relationship: CourseInstructorCrossRef
    )

    //find all instructors associated with a course
    @Query("""
        SELECT instructors.*
        FROM instructors
        INNER JOIN course_instructors
        ON instructors.instructorId = course_instructors.instructorId
        WHERE course_instructors.courseId = :courseId
        ORDER BY instructors.name ASC
    """)
    fun getInstructorsForCourse(
        courseId: String
    ): Flow<List<InstructorEntity>>

    //find all courses associated with an instructor
    @Query("""
        SELECT courses.*
        FROM courses
        INNER JOIN course_instructors
        ON courses.id = course_instructors.courseId
        WHERE course_instructors.instructorId = :instructorId
        ORDER BY courses.id ASC
    """)
    fun getCoursesForInstructor(
        instructorId: String
    ): Flow<List<CourseEntity>>
}