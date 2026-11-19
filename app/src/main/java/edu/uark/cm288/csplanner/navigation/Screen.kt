package edu.uark.cm288.csplanner.navigation

import android.net.Uri

// navigation component for moving between screens
sealed class Screen(val route: String) {
    data object Entry : Screen("entry")
    data object Onboarding : Screen("onboarding")
    data object Recommendation : Screen("recommendation")
    data object DegreePath : Screen("degree_path")
    data object Instructors : Screen("instructors")

    data object Catalog : Screen("catalog?forFeedback={forFeedback}") {
        fun createRoute(forFeedback: Boolean = false) = "catalog?forFeedback=$forFeedback"
    }

    data object CourseOverview : Screen("overview/{courseId}") {
        fun createRoute(courseId: String) = "overview/${Uri.encode(courseId)}"
    }

    data object Feedback : Screen("feedback/{courseId}") {
        fun createRoute(courseId: String) = "feedback/${Uri.encode(courseId)}"
    }
}
