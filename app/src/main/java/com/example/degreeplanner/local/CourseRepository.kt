package com.example.degreeplanner.local

import kotlinx.coroutines.flow.Flow

//provides course info to other parts of the app
class CourseRepository(
    private val courseDao: CourseDao
) {

    //get all courses from database
    fun getAllCourses(): Flow<List<CourseEntity>> {
        return courseDao.getAllCourses()
    }

    //gat a specific course using its id
    suspend fun getCourseById(
        courseId: String
    ): CourseEntity? {
        return courseDao.getCourseById(courseId)
    }

    //search courses by id or name
    fun searchCourses(
        search: String
    ): Flow<List<CourseEntity>> {
        return courseDao.searchCourses(search)
    }

    //get total number of stored courses
    suspend fun getCourseCount(): Int {
        return courseDao.getCourseCount()
    }
}