package com.example.degreeplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.degreeplanner.ui.theme.DegreePlannerTheme
import android.util.Log
import com.example.degreeplanner.data.repository.DegreeRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = DegreeRepository(this)
        val courses = repository.loadCourses()

        Log.d("DegreePlanner", "Loaded ${courses.size} courses")

        val template = repository.loadDegreeTemplate()

        Log.d("DegreePlanner", "Degree: ${template.degreeName}")
        Log.d("DegreePlanner", "Credits: ${template.totalCreditHours}")
        Log.d("DegreePlanner", "Courses: ${template.coursePlacements.size}")
        Log.d("DegreePlanner", "Electives: ${template.electiveSlots.size}")

        setContent {
            DegreePlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DegreePlannerTheme {
        Greeting("Android")
    }
}