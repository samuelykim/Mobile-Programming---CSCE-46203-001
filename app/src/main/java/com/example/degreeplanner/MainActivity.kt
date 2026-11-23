package com.example.degreeplanner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

import com.example.degreeplanner.data.repository.DegreeRepository
import com.example.degreeplanner.navigation.AppNavHost
import com.example.degreeplanner.ui.theme.CSPlannerTheme
import com.example.degreeplanner.planner.DegreePlanGenerator

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sam's JSON loading
        val repository = DegreeRepository(this)
        val courses = repository.loadCourses()

        Log.d("DegreePlanner", "Loaded ${courses.size} courses")

        val template = repository.loadDegreeTemplate()

        Log.d("DegreePlanner", "Degree: ${template.degreeName}")
        Log.d("DegreePlanner", "Credits: ${template.totalCreditHours}")
        Log.d("DegreePlanner", "Courses: ${template.coursePlacements.size}")
        Log.d("DegreePlanner", "Electives: ${template.electiveSlots.size}")


        val generator = DegreePlanGenerator()
        val plan = generator.generatePlan(courses, template)

        Log.d("DegreePlanner", "Generated: ${plan.degreeName}")

        for (semester in plan.semesters) {

            Log.d(
                "DegreePlanner",
                "Semester ${semester.semesterNumber}: " +
                        "${semester.courses.size} courses, " +
                        "${semester.electives.size} electives, " +
                        "${semester.totalCredits} credits"
            )
        }

        Log.d(
            "DegreePlanner",
            "Total degree credits: ${plan.totalCredits}"
        )

        for (warning in plan.warnings) {
            Log.w("DegreePlanner", warning)
        }


        // Cade's UI and navigation
        setContent {
            CSPlannerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavHost()
                    }
                }
            }
        }
    }
}
