package com.example.degreeplanner.local

import android.content.Context
import org.json.JSONArray

object CourseInstructorDataLoader {

    fun load(context: Context): List<CourseInstructorCrossRef> {

        //read the instructor course history JSON file
        val json = context.assets.open(
            "CS_Degree_Planner_Instructor_Course_History.json"
        ).bufferedReader().use {
            it.readText()
        }

        val jsonArray = JSONArray(json)

        val relationships = mutableListOf<CourseInstructorCrossRef>()

        //read each instructor and course pair
        for (i in 0 until jsonArray.length()) {

            val item = jsonArray.getJSONObject(i)

            val instructorId = item.getString("instructorId")
            val courseId = item.getString("courseId")

            relationships.add(
                CourseInstructorCrossRef(
                    courseId = courseId,
                    instructorId = instructorId
                )
            )
        }

        //prevent duplicate relationships
        return relationships.distinct()
    }
}
