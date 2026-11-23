package com.example.degreeplanner.data.model

data class StudentPreferences(
    val attendsUark: Boolean = true,
    val degreeId: String = "BSCS_2026_27",
    val degreeName: String = "Computer Science B.S.",
    val selectedInterests: Set<String> = emptySet(),
    val targetCreditHoursPerSemester: Int = 15,
    val workloadPreference: WorkloadPreference = WorkloadPreference.BALANCED
)

enum class WorkloadPreference(val label: String, val description: String, val defaultHours: Int) {
    LIGHT("Light Workload", "12 – 13 credit hours / semester", 12),
    BALANCED("Balanced Workload", "14 – 16 credit hours / semester", 15),
    HEAVY("Heavy Workload", "17 – 18 credit hours / semester", 17)
}
