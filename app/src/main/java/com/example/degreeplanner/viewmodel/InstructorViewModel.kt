package com.example.degreeplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeplanner.data.model.Instructor
import com.example.degreeplanner.local.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InstructorListUiState(
    val searchQuery: String = "",
    val instructors: List<Instructor> = emptyList(),
    val filteredInstructors: List<Instructor> = emptyList()
)

class InstructorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)

    private val instructorDao = database.instructorDao()
    private val relationshipDao = database.courseInstructorDao()

    private val _uiState = MutableStateFlow(InstructorListUiState())
    val uiState: StateFlow<InstructorListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

            //load the 48 instructors from yoom
            instructorDao.getAllInstructors().collectLatest { roomInstructors ->

                if (roomInstructors.isEmpty()) {
                    updateInstructorList(emptyList())
                } else {

                    //observe courses associated with each instructor
                    val courseFlows = roomInstructors.map { instructor ->
                        relationshipDao.getCoursesForInstructor(
                            instructor.instructorId
                        )
                    }

                    combine(courseFlows) { coursesByInstructor ->

                        roomInstructors.mapIndexed { index, instructor ->

                            // Convert Room courses into Cade's display format
                            val courseNames = coursesByInstructor[index].map { course ->
                                "${course.id}: ${course.name}"
                            }

                            Instructor(
                                id = instructor.instructorId,
                                name = instructor.name,
                                title = instructor.department,
                                email = instructor.email.orEmpty(),
                                taughtCourses = courseNames
                            )
                        }
                    }.collect { instructors ->
                        updateInstructorList(instructors)
                    }
                }
            }
        }
    }

    private fun updateInstructorList(instructors: List<Instructor>) {
        _uiState.update { state ->
            state.copy(
                instructors = instructors,
                filteredInstructors = filterInstructors(
                    instructors,
                    state.searchQuery
                )
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredInstructors = filterInstructors(
                    state.instructors,
                    query
                )
            )
        }
    }

    private fun filterInstructors(
        instructors: List<Instructor>,
        query: String
    ): List<Instructor> {

        val search = query.trim().lowercase()

        return instructors.filter { instructor ->
            search.isEmpty() ||
                    instructor.name.lowercase().contains(search) ||
                    instructor.title.lowercase().contains(search) ||
                    instructor.taughtCourses.any {
                        it.lowercase().contains(search)
                    }
        }
    }
}
