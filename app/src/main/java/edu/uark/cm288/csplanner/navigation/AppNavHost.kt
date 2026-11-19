package edu.uark.cm288.csplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.uark.cm288.csplanner.ui.screens.catalog.CourseCatalogScreen
import edu.uark.cm288.csplanner.ui.screens.catalog.CourseOverviewScreen
import edu.uark.cm288.csplanner.ui.screens.degreepath.DegreePathScreen
import edu.uark.cm288.csplanner.ui.screens.entry.EntryScreen
import edu.uark.cm288.csplanner.ui.screens.feedback.CourseFeedbackScreen
import edu.uark.cm288.csplanner.ui.screens.instructors.InstructorListScreen
import edu.uark.cm288.csplanner.ui.screens.onboarding.OnboardingScreen
import edu.uark.cm288.csplanner.ui.screens.recommendation.RecommendationScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Entry.route
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
            OnboardingScreen(onNavigateTo = { route -> navController.navigate(route) })
        }
        composable(Screen.Recommendation.route) {
            RecommendationScreen(onNavigateTo = { route -> navController.navigate(route) })
        }
        composable(Screen.DegreePath.route) {
            DegreePathScreen(onNavigateTo = { route -> navController.navigate(route) })
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
