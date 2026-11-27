package com.example.degreeplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.degreeplanner.data.model.Course
import com.example.degreeplanner.data.model.DegreeOption
import com.example.degreeplanner.data.model.FullDegreePlan
import com.example.degreeplanner.data.model.StudentPreferences
import com.example.degreeplanner.data.model.WorkloadPreference
import com.example.degreeplanner.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import com.example.degreeplanner.local.AppDatabase
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val preferences: StudentPreferences = StudentPreferences(),
    val availableDegrees: List<DegreeOption> = emptyList(),
    val availableInterests: List<String> = emptyList(),
    val allCourses: List<Course> = emptyList(),
    val degreePlan: FullDegreePlan? = null,
    val isCompleted: Boolean = false
)

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlannerRepository(application)

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        //load degree choices and interest options
        val degrees = repository.loadDegreeOptions()
        val interests = repository.loadInterestOptions()

        val initialDegree = degrees.firstOrNull()

        val initialPrefs = StudentPreferences(
            degreeId = initialDegree?.degreeId ?: "BSCS_2026_27",
            degreeName = initialDegree?.name ?: "Computer Science B.S."
        )

        _uiState.update { state ->
            state.copy(
                availableDegrees = degrees,
                availableInterests = interests,
                preferences = initialPrefs
            )
        }

        //load courses from your room database
        val courseDao = AppDatabase
            .getDatabase(application)
            .courseDao()

        viewModelScope.launch {
            courseDao.getAllCourses().collect { roomCourses ->

                //wait until the database contains courses
                if (roomCourses.isEmpty()) return@collect

                //convert room courses into Sams course model
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
                        instructors = emptyList(),
                        previousSchedules = emptyList(),
                        averageRating = 0f,
                        difficultyRating = 0f,
                        studentFeedback = emptyList()
                    )
                }

                _uiState.update { state ->
                    state.copy(
                        allCourses = courses,

                        //generate the degree plan using room data
                        degreePlan = repository.generateDegreePlan(
                            state.preferences,
                            courses
                        )
                    )
                }
            }
        }
    }

    private fun recalculatePlan(
        updatedPrefs: StudentPreferences
    ): FullDegreePlan? {

        val courses = _uiState.value.allCourses

        // Don't generate the plan until Room has loaded
        if (courses.isEmpty()) return null

        return repository.generateDegreePlan(
            updatedPrefs,
            courses
        )
    }

    fun setAttendsUark(attends: Boolean) {
        _uiState.update { state ->
            val newPrefs = state.preferences.copy(attendsUark = attends)
            state.copy(preferences = newPrefs, degreePlan = recalculatePlan(newPrefs))
        }
    }

    fun setDegree(degreeId: String, degreeName: String) {
        _uiState.update { state ->
            val newPrefs = state.preferences.copy(degreeId = degreeId, degreeName = degreeName)
            state.copy(preferences = newPrefs, degreePlan = recalculatePlan(newPrefs))
        }
    }

    fun toggleInterest(interest: String) {
        _uiState.update { state ->
            val current = state.preferences.selectedInterests
            val updated = if (current.contains(interest)) {
                current - interest
            } else {
                current + interest
            }
            val newPrefs = state.preferences.copy(selectedInterests = updated)
            state.copy(preferences = newPrefs, degreePlan = recalculatePlan(newPrefs))
        }
    }

    fun setTargetCreditHours(hours: Int) {
        _uiState.update { state ->
            val newPrefs = state.preferences.copy(targetCreditHoursPerSemester = hours)
            state.copy(preferences = newPrefs, degreePlan = recalculatePlan(newPrefs))
        }
    }

    fun setWorkloadPreference(workload: WorkloadPreference) {
        _uiState.update { state ->
            val newPrefs = state.preferences.copy(
                workloadPreference = workload,
                targetCreditHoursPerSemester = workload.defaultHours
            )
            state.copy(preferences = newPrefs, degreePlan = recalculatePlan(newPrefs))
        }
    }

    fun nextPage(): Boolean {
        var completed = false
        _uiState.update { state ->
            if (state.currentStep < state.totalSteps - 1) {
                state.copy(currentStep = state.currentStep + 1)
            } else {
                completed = true
                state.copy(isCompleted = true)
            }
        }
        return completed
    }

    fun previousPage() {
        _uiState.update { state ->
            if (state.currentStep > 0) {
                state.copy(currentStep = state.currentStep - 1)
            } else {
                state
            }
        }
    }

    fun goToStep(step: Int) {
        _uiState.update { state ->
            if (step in 0 until state.totalSteps) {
                state.copy(currentStep = step)
            } else {
                state
            }
        }
    }

    fun resetCompletion() {
        _uiState.update { state ->
            state.copy(isCompleted = false, currentStep = 0)
        }
    }
}
