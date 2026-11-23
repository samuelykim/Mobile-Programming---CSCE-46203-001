package com.example.degreeplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.degreeplanner.data.model.Course
import com.example.degreeplanner.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CourseCatalogUiState(
    val searchQuery: String = "",
    val selectedFilterTag: String? = null,
    val availableFilterTags: List<String> = emptyList(),
    val allCourses: List<Course> = emptyList(),
    val filteredCourses: List<Course> = emptyList()
)

class CourseCatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlannerRepository(application)

    private val _uiState = MutableStateFlow(CourseCatalogUiState())
    val uiState: StateFlow<CourseCatalogUiState> = _uiState.asStateFlow()

    init {
        val courses = repository.loadCourses()
        val tags = repository.loadInterestOptions()
        _uiState.update {
            it.copy(
                allCourses = courses,
                filteredCourses = courses,
                availableFilterTags = tags
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val updatedState = state.copy(searchQuery = query)
            updatedState.copy(filteredCourses = filterCoursesList(updatedState))
        }
    }

    fun onFilterTagSelect(tag: String?) {
        _uiState.update { state ->
            val newTag = if (state.selectedFilterTag == tag) null else tag
            val updatedState = state.copy(selectedFilterTag = newTag)
            updatedState.copy(filteredCourses = filterCoursesList(updatedState))
        }
    }

    private fun filterCoursesList(state: CourseCatalogUiState): List<Course> {
        val query = state.searchQuery.trim().lowercase()
        val tag = state.selectedFilterTag

        return state.allCourses.filter { course ->
            val matchesQuery = query.isEmpty() ||
                    course.id.lowercase().contains(query) ||
                    course.name.lowercase().contains(query) ||
                    course.instructors.any { it.lowercase().contains(query) } ||
                    course.description.lowercase().contains(query)

            val matchesTag = tag == null || course.interestTags.contains(tag)

            matchesQuery && matchesTag
        }
    }
}
