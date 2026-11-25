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

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.degreeplanner.firebase.FirebaseAuthManager

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.degreeplanner.local.CourseDatabaseInitializer
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //load course and instructor data into room
        lifecycleScope.launch {
            try {
                CourseDatabaseInitializer().initialize(applicationContext)

                Log.d(
                    "TeamRoom",
                    "Room database initialized successfully"
                )

            } catch (e: Exception) {
                Log.e(
                    "TeamRoom",
                    "Room database initialization failed",
                    e
                )
            }
        }

        //test firebase authentication in the team app
        lifecycleScope.launch {
            try {
                //sign in using the existing firebase project
                FirebaseAuthManager().signInAnonymously()

                //confirm that authentication worked
                Log.d("TeamFirebase", "Firebase authentication successful")

                Toast.makeText(
                    this@MainActivity,
                    "Team Firebase connected!",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {

                //display an error if authentication fails
                Log.e("TeamFirebase", "Authentication failed", e)

                Toast.makeText(
                    this@MainActivity,
                    "Firebase error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

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
