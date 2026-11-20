package com.example.degreeplanner.data.repository

import android.content.Context
import com.example.degreeplanner.data.model.Course
import org.json.JSONArray
import com.example.degreeplanner.data.model.DegreeTemplate
import com.example.degreeplanner.data.model.CoursePlacement
import com.example.degreeplanner.data.model.ElectiveSlot
import org.json.JSONObject

class DegreeRepository(private val context: Context) {

    fun loadCourses(): List<Course> {
        val jsonString = context.assets
            .open("CS_Degree_Planner_Courses.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonArray = JSONArray(jsonString)
        val courses = mutableListOf<Course>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val prerequisites = mutableListOf<String>()
            val prerequisiteArray =
                jsonObject.optJSONArray("prerequisiteCourseIds")

            if (prerequisiteArray != null) {
                for (j in 0 until prerequisiteArray.length()) {
                    prerequisites.add(prerequisiteArray.getString(j))
                }
            }

            val terms = mutableListOf<String>()
            val termsArray = jsonObject.optJSONArray("typicalTerms")

            if (termsArray != null) {
                for (j in 0 until termsArray.length()) {
                    terms.add(termsArray.getString(j))
                }
            }

            val tags = mutableListOf<String>()
            val tagsArray = jsonObject.optJSONArray("interestTags")

            if (tagsArray != null) {
                for (j in 0 until tagsArray.length()) {
                    tags.add(tagsArray.getString(j))
                }
            }

            val course = Course(
                id = jsonObject.getString("id"),
                name = jsonObject.getString("name"),
                creditHours = jsonObject.getInt("creditHours"),
                description = jsonObject.optString("description"),
                prerequisiteText =
                    jsonObject.optString("prerequisiteText"),
                prerequisiteCourseIds = prerequisites,
                prerequisiteLogicComplete =
                    jsonObject.optBoolean("prerequisiteLogicComplete"),
                typicalTerms = terms,
                interestTags = tags
            )

            courses.add(course)
        }

        return courses
    }

    fun getCourseById(id: String): Course? {
        return loadCourses().find { it.id == id }
    }


    fun loadDegreeTemplate(): DegreeTemplate {

        val jsonString = context.assets
            .open("CS_Degree_Planner_Template.json")
            .bufferedReader()
            .use { it.readText() }

        val json = JSONObject(jsonString)

        val placements = mutableListOf<CoursePlacement>()
        val placementArray = json.getJSONArray("coursePlacements")

        for (i in 0 until placementArray.length()) {
            val item = placementArray.getJSONObject(i)

            placements.add(
                CoursePlacement(
                    courseId = item.getString("courseId"),
                    semesterNumber = item.getInt("semesterNumber")
                )
            )
        }

        val electives = mutableListOf<ElectiveSlot>()
        val electiveArray = json.getJSONArray("electiveSlots")

        for (i in 0 until electiveArray.length()) {
            val item = electiveArray.getJSONObject(i)

            electives.add(
                ElectiveSlot(
                    requirementId = item.getString("requirementId"),
                    name = item.getString("name"),
                    semesterNumber = item.getInt("semesterNumber"),
                    creditHours = item.getInt("creditHours")
                )
            )
        }

        return DegreeTemplate(
            degreeId = json.getString("degreeId"),
            degreeName = json.getString("degreeName"),
            catalogYear = json.getString("catalogYear"),
            totalCreditHours = json.getInt("totalCreditHours"),
            coursePlacements = placements,
            electiveSlots = electives
        )
    }

}
