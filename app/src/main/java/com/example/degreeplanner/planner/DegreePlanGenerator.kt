package com.example.degreeplanner.planner

import com.example.degreeplanner.data.model.Course
import com.example.degreeplanner.data.model.DegreeTemplate
import com.example.degreeplanner.data.model.DegreePlan
import com.example.degreeplanner.data.model.SemesterCourseItem
import com.example.degreeplanner.data.model.SemesterPlan

class DegreePlanGenerator {

    fun generatePlan(
        courses: List<Course>,
        template: DegreeTemplate
    ): DegreePlan {

        val courseMap = courses.associateBy { it.id }

        val semesters = mutableListOf<SemesterPlan>()
        val warnings = mutableListOf<String>()

        // Generate all 8 semesters
        for (semesterNumber in 1..8) {

            // Find courses assigned to this semester
            val semesterCourses = template.coursePlacements
                .filter {
                    it.semesterNumber == semesterNumber
                }
                .mapNotNull { placement ->

                    val course = courseMap[placement.courseId]

                    if (course == null) {
                        warnings.add(
                            "Missing course: ${placement.courseId}"
                        )
                    }

                    course
                }

            // Find elective requirements
            val semesterElectives = template.electiveSlots
                .filter {
                    it.semesterNumber == semesterNumber
                }

            // Calculate credit hours
            val courseCredits = semesterCourses
                .sumOf { it.creditHours }

            val electiveCredits = semesterElectives
                .sumOf { it.creditHours }

            val totalCredits = courseCredits + electiveCredits

            // Create the semester
            semesters.add(
                SemesterPlan(
                    semesterNumber = semesterNumber,
                    semesterName = "Semester $semesterNumber",
                    courses = semesterCourses.map { course ->
                        SemesterCourseItem(
                            courseId = course.id,
                            courseName = course.name,
                            creditHours = course.creditHours,
                            isElective = false,
                            description = course.description
                        )
                    },
                    rawCourses = semesterCourses,
                    electives = semesterElectives,
                    totalCreditHours = totalCredits,
                    totalCredits = totalCredits
                )
            )
        }

        // Calculate total degree credits
        val totalCredits = semesters.sumOf {
            it.totalCredits
        }

        // Check expected credit hours
        if (totalCredits != template.totalCreditHours) {
            warnings.add(
                "Expected ${template.totalCreditHours} credits, " +
                        "but calculated $totalCredits"
            )
        }

        return DegreePlan(
            degreeName = template.degreeName,
            semesters = semesters,
            totalCredits = totalCredits,
            warnings = warnings
        )
    }
}
