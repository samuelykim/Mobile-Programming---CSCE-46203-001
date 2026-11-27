package com.example.degreeplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.degreeplanner.data.model.Course
import com.example.degreeplanner.data.repository.PlannerRepository
import com.example.degreeplanner.local.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CourseDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    //cades repository for compatibility
    private val repository = PlannerRepository(application)

    //room database
    private val database = AppDatabase.getDatabase(application)

    private val courseDao = database.courseDao()
    private val relationshipDao = database.courseInstructorDao()

    fun getCourseById(courseId: String): Course? {
        return repository.getCourseById(courseId)
    }

    //load course information and associated instructors from room
    fun observeCourse(courseId: String): Flow<Course?> {

        return combine(
            courseDao.getAllCourses(),
            relationshipDao.getInstructorsForCourse(courseId)
        ) { roomCourses, roomInstructors ->

            //find the selected course
            val entity = roomCourses.find {
                it.id.equals(courseId, ignoreCase = true)
            }

            //get the associated instructor names
            val instructorNames = roomInstructors
                .map { it.name }
                .distinct()
                .sorted()

            //convert room data into cades course model
            entity?.let {
                Course(
                    id = it.id,
                    name = it.name,
                    creditHours = it.creditHours,
                    description = it.description,
                    prerequisiteText = it.prerequisiteText.orEmpty(),
                    prerequisiteCourseIds = it.prerequisiteCourseIds,
                    prerequisiteLogicComplete = it.prerequisiteLogicComplete,
                    typicalTerms = it.typicalTerms,
                    interestTags = it.interestTags,

                    //instructor names from your room relationships
                    instructors = instructorNames,

                    //no verified schedule history yet
                    previousSchedules = emptyList(),

                    //firebase ratings load separately
                    averageRating = 0f,
                    difficultyRating = 0f,
                    studentFeedback = emptyList()
                )
            }
        }
    }
}
