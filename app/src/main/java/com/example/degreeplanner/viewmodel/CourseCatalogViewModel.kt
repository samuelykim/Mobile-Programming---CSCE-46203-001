package com.example.degreeplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeplanner.data.model.Course
import com.example.degreeplanner.data.repository.PlannerRepository
import com.example.degreeplanner.local.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CourseCatalogUiState(
    val searchQuery: String = "",
    val selectedFilterTag: String? = null,
    val availableFilterTags: List<String> = emptyList(),
    val allCourses: List<Course> = emptyList(),
    val filteredCourses: List<Course> = emptyList()
)

class CourseCatalogViewModel(
    application: Application
) : AndroidViewModel(application) {

    //sams repository for interest filter options
    private val plannerRepository = PlannerRepository(application)

    //room database for the actual course list
    private val courseDao = AppDatabase
        .getDatabase(application)
        .courseDao()

    private val _uiState = MutableStateFlow(CourseCatalogUiState())
    val uiState: StateFlow<CourseCatalogUiState> = _uiState.asStateFlow()

    init {
        // interest filters
        val tags = plannerRepository.loadInterestOptions()

        _uiState.update {
            it.copy(availableFilterTags = tags)
        }

        //automatically observe courses stored in room
        viewModelScope.launch {
            courseDao.getAllCourses().collect { roomCourses ->

                //convert room entities into Cades course model
                val courses = roomCourses.map { entity ->
                    Course(
                        id = entity.id,
                        name = entity.name,
                        creditHours = entity.creditHours,
                        description = entity.description,
                        prerequisiteText = entity.prerequisiteText.orEmpty(),
                        prerequisiteCourseIds = entity.prerequisiteCourseIds,
                        prerequisiteLogicComplete = entity.prerequisiteLogicComplete,
                        typicalTerms = entity.typicalTerms,
                        interestTags = entity.interestTags,

                        //we will connect verified instructors later
                        instructors = emptyList(),

                        //no verified schedules stored in Room yet
                        previousSchedules = emptyList(),

                        //dont display invented sample ratings
                        averageRating = 0f,
                        difficultyRating = 0f,
                        studentFeedback = emptyList()
                    )
                }

                _uiState.update { state ->
                    val updatedState = state.copy(allCourses = courses)

                    updatedState.copy(
                        filteredCourses = filterCoursesList(updatedState)
                    )
                }
            }
        }
    }

    //search for courses
    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val updatedState = state.copy(searchQuery = query)

            updatedState.copy(
                filteredCourses = filterCoursesList(updatedState)
            )
        }
    }

    //filter by student interest
    fun onFilterTagSelect(tag: String?) {
        _uiState.update { state ->
            val newTag =
                if (state.selectedFilterTag == tag) null else tag

            val updatedState = state.copy(selectedFilterTag = newTag)

            updatedState.copy(
                filteredCourses = filterCoursesList(updatedState)
            )
        }
    }

    //cades existing filtering behavior
    private fun filterCoursesList(
        state: CourseCatalogUiState
    ): List<Course> {

        val query = state.searchQuery.trim().lowercase()
        val tag = state.selectedFilterTag

        return state.allCourses.filter { course ->

            val matchesQuery =
                query.isEmpty() ||
                        course.id.lowercase().contains(query) ||
                        course.name.lowercase().contains(query) ||
                        course.instructors.any {
                            it.lowercase().contains(query)
                        } ||
                        course.description.lowercase().contains(query)

            val matchesTag =
                tag == null || course.interestTags.contains(tag)

            matchesQuery && matchesTag
        }
    }
}
