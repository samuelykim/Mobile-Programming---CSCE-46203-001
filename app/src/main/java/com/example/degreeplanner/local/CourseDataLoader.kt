package com.example.degreeplanner.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

//reads course info from JSON and save it to Room
class CourseDataLoader {

    suspend fun loadCourses(
        context: Context,
        courseDao: CourseDao
    ): Int = withContext(Dispatchers.IO) {

        //open and read the JSON file from assets
        val jsonString = context.assets
            .open("CS_Degree_Planner_Courses.json")
            .bufferedReader()
            .use { it.readText() }

        //convert the JSON text into a list of courses
        val jsonArray = JSONArray(jsonString)
        val courses = mutableListOf<CourseEntity>()

        for (i in 0 until jsonArray.length()) {

            val jsonCourse = jsonArray.getJSONObject(i)

            //convert each JSON course into a room entity
            val course = CourseEntity(
                id = jsonCourse.getString("id"),
                name = jsonCourse.getString("name"),
                creditHours = jsonCourse.getInt("creditHours"),
                description = jsonCourse.getString("description"),

                prerequisiteText =
                    if (jsonCourse.isNull("prerequisiteText")) {
                        null
                    } else {
                        jsonCourse.getString("prerequisiteText")
                    },

                prerequisiteCourseIds = getStringList(
                    jsonCourse,
                    "prerequisiteCourseIds"
                ),

                prerequisiteLogicComplete = jsonCourse.getBoolean(
                    "prerequisiteLogicComplete"
                ),

                typicalTerms = getStringList(
                    jsonCourse,
                    "typicalTerms"
                ),

                interestTags = getStringList(
                    jsonCourse,
                    "interestTags"
                )
            )

            courses.add(course)
        }

        //save all courses into room
        courseDao.insertCourses(courses)

        //return how many courses are imported
        courses.size
    }

    //converts a JSON array into a kotlin string list
    private fun getStringList(
        json: JSONObject,
        field: String
    ): List<String> {

        val array = json.getJSONArray(field)

        return List(array.length()) { index ->
            array.getString(index)
        }
    }
}