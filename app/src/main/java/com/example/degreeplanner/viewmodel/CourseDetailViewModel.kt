package com.example.degreeplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.degreeplanner.data.model.Course
import com.example.degreeplanner.data.repository.PlannerRepository

class CourseDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlannerRepository(application)

    fun getCourseById(courseId: String): Course? {
        return repository.getCourseById(courseId)
    }
}
