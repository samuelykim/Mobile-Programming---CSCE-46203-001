package com.example.degreeplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.degreeplanner.ui.screens.catalog.CourseCatalogScreen
import com.example.degreeplanner.ui.screens.catalog.CourseOverviewScreen
import com.example.degreeplanner.ui.screens.degreepath.DegreePathScreen
import com.example.degreeplanner.ui.screens.entry.EntryScreen
import com.example.degreeplanner.ui.screens.feedback.CourseFeedbackScreen
import com.example.degreeplanner.ui.screens.instructors.InstructorListScreen
import com.example.degreeplanner.ui.screens.onboarding.OnboardingScreen
import com.example.degreeplanner.ui.screens.recommendation.RecommendationScreen
import com.example.degreeplanner.viewmodel.OnboardingViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Entry.route,
    onboardingViewModel: OnboardingViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Entry.route) {
            EntryScreen(onNavigateTo = { route -> navController.navigate(route) })
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }
        composable(Screen.Recommendation.route) {
            RecommendationScreen(
                viewModel = onboardingViewModel,
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }
        composable(Screen.DegreePath.route) {
            DegreePathScreen(
                viewModel = onboardingViewModel,
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }
        composable(Screen.Instructors.route) {
            InstructorListScreen(onNavigateTo = { route -> navController.navigate(route) })
        }
        composable(
            route = Screen.Catalog.route,
            arguments = listOf(
                navArgument("forFeedback") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val forFeedback = backStackEntry.arguments?.getBoolean("forFeedback") ?: false
            CourseCatalogScreen(
                forFeedback = forFeedback,
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }
        composable(
            route = Screen.CourseOverview.route,
            arguments = listOf(
                navArgument("courseId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: "Unknown"
            CourseOverviewScreen(
                courseId = courseId,
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }
        composable(
            route = Screen.Feedback.route,
            arguments = listOf(
                navArgument("courseId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: "Unknown"
            CourseFeedbackScreen(
                courseId = courseId,
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }
    }
}
