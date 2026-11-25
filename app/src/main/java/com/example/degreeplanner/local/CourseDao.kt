package com.example.degreeplanner.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//handles adding, retrieving, and searching courses
@Dao
interface CourseDao {

    //add courses to the database
    //replace a course if its id already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    //get every course in alphabetical order
    @Query("SELECT * FROM courses ORDER BY id ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    //find a specific course using its id
    @Query("SELECT * FROM courses WHERE id = :courseId LIMIT 1")
    suspend fun getCourseById(courseId: String): CourseEntity?

    //look by course id or course name
    @Query("""
        SELECT * FROM courses
        WHERE id LIKE '%' || :search || '%'
        OR name LIKE '%' || :search || '%'
        ORDER BY id ASC
    """)
    fun searchCourses(search: String): Flow<List<CourseEntity>>

    //count how many courses are stored
    @Query("SELECT COUNT(*) FROM courses")
    suspend fun getCourseCount(): Int
}