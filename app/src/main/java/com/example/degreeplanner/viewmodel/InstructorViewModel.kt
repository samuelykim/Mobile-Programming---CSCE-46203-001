package com.example.degreeplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.degreeplanner.data.model.Instructor
import com.example.degreeplanner.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class InstructorListUiState(
    val searchQuery: String = "",
    val instructors: List<Instructor> = emptyList(),
    val filteredInstructors: List<Instructor> = emptyList()
)

class InstructorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlannerRepository(application)

    private val _uiState = MutableStateFlow(InstructorListUiState())
    val uiState: StateFlow<InstructorListUiState> = _uiState.asStateFlow()

    init {
        val list = repository.loadInstructors()
        _uiState.update {
            it.copy(instructors = list, filteredInstructors = list)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val q = query.trim().lowercase()
            val filtered = state.instructors.filter { inst ->
                q.isEmpty() ||
                        inst.name.lowercase().contains(q) ||
                        inst.title.lowercase().contains(q) ||
                        inst.taughtCourses.any { c -> c.lowercase().contains(q) }
            }
            state.copy(searchQuery = query, filteredInstructors = filtered)
        }
    }
}
