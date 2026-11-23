package com.example.degreeplanner.data.repository

import android.content.Context
import com.example.degreeplanner.data.model.CourseRatingMetrics
import com.example.degreeplanner.data.model.DetailedCourseFeedback

class FeedbackRepository(private val context: Context) {

    private val reviewsStore = mutableMapOf<String, MutableList<DetailedCourseFeedback>>()

    init {
        // Sample initial feedback reviews for courses
        reviewsStore["CSCE 20004"] = mutableListOf(
            DetailedCourseFeedback(
                id = "f1",
                courseId = "CSCE 20004",
                studentName = "Alex M.",
                term = "Fall 2025",
                overallRating = 5.0f,
                difficultyRating = 3.5f,
                workloadRating = 3.8f,
                organizationRating = 5.0f,
                comment = "Great foundational C++ programming course! Clear projects and helpful labs."
            ),
            DetailedCourseFeedback(
                id = "f2",
                courseId = "CSCE 20004",
                studentName = "Jordan P.",
                term = "Spring 2025",
                overallRating = 4.5f,
                difficultyRating = 3.8f,
                workloadRating = 4.0f,
                organizationRating = 4.5f,
                comment = "Challenging programming assignments, but excellent professor support during office hours."
            )
        )

        reviewsStore["CSCE 20104"] = mutableListOf(
            DetailedCourseFeedback(
                id = "f3",
                courseId = "CSCE 20104",
                studentName = "Taylor S.",
                term = "Fall 2025",
                overallRating = 4.8f,
                difficultyRating = 4.0f,
                workloadRating = 4.2f,
                organizationRating = 4.6f,
                comment = "Essential data structures & OOP in C++. Start programming assignments early!"
            )
        )

        reviewsStore["CSCE 31903"] = mutableListOf(
            DetailedCourseFeedback(
                id = "f4",
                courseId = "CSCE 31903",
                studentName = "Chris B.",
                term = "Fall 2025",
                overallRating = 4.5f,
                difficultyRating = 3.2f,
                workloadRating = 3.5f,
                organizationRating = 4.8f,
                comment = "Loved exploring functional, logic, and concurrent programming paradigms."
            )
        )
    }

    fun getCourseName(courseId: String): String {
        val repo = PlannerRepository(context)
        val course = repo.getCourseById(courseId)
        return course?.name ?: "Computer Science Course"
    }

    fun getReviewsForCourse(courseId: String): List<DetailedCourseFeedback> {
        return reviewsStore[courseId] ?: listOf(
            DetailedCourseFeedback(
                id = "f_def",
                courseId = courseId,
                studentName = "Department Student",
                term = "Fall 2025",
                overallRating = 4.5f,
                difficultyRating = 3.5f,
                workloadRating = 3.5f,
                organizationRating = 4.5f,
                comment = "Solid course covering core computer science topics."
            )
        )
    }

    fun getCourseMetrics(courseId: String): CourseRatingMetrics {
        val reviews = getReviewsForCourse(courseId)
        if (reviews.isEmpty()) return CourseRatingMetrics()

        val avgOverall = reviews.map { it.overallRating }.average().toFloat()
        val avgDiff = reviews.map { it.difficultyRating }.average().toFloat()
        val avgWorkload = reviews.map { it.workloadRating }.average().toFloat()
        val avgOrg = reviews.map { it.organizationRating }.average().toFloat()

        return CourseRatingMetrics(
            overallRating = (Math.round(avgOverall * 10) / 10.0f),
            difficultyRating = (Math.round(avgDiff * 10) / 10.0f),
            workloadRating = (Math.round(avgWorkload * 10) / 10.0f),
            organizationRating = (Math.round(avgOrg * 10) / 10.0f),
            totalSubmissions = reviews.size
        )
    }

    fun saveFeedback(feedback: DetailedCourseFeedback) {
        val list = reviewsStore.getOrPut(feedback.courseId) { mutableListOf() }
        // Update if existing submission by student, otherwise add
        val existingIndex = list.indexOfFirst { it.studentName == feedback.studentName }
        if (existingIndex >= 0) {
            list[existingIndex] = feedback
        } else {
            list.add(0, feedback)
        }
    }
}
