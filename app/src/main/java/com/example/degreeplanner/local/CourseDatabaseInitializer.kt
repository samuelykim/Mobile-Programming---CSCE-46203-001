package com.example.degreeplanner.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

//initializes course, instructor, and relationship data in Room
class CourseDatabaseInitializer {

    suspend fun initialize(context: Context): Int =
        withContext(Dispatchers.IO) {

            val appContext = context.applicationContext

            //connect to the database
            val database = AppDatabase.getDatabase(appContext)

            val courseDao = database.courseDao()
            val instructorDao = database.instructorDao()
            val relationshipDao = database.courseInstructorDao()

            //add courses if the table is empty
            if (courseDao.getCourseCount() == 0) {
                CourseDataLoader().loadCourses(
                    appContext,
                    courseDao
                )
            }

            //add instructors if the table is empty
            if (instructorDao.getInstructorCount() == 0) {
                InstructorDataLoader().loadInstructors(
                    appContext,
                    instructorDao
                )
            }

            // load student reported course history
            // courses and instructors must exist first
            val relationships = CourseInstructorDataLoader.load(appContext)

            // Add relationships without duplicates
            for (relationship in relationships) {

                val existingCourses = relationshipDao
                    .getCoursesForInstructor(relationship.instructorId)
                    .first()

                val alreadyExists = existingCourses.any {
                    it.id == relationship.courseId
                }

                if (!alreadyExists) {
                    relationshipDao.addCourseInstructor(relationship)
                }
            }

            //return the total number of courses
            courseDao.getCourseCount()
        }
}