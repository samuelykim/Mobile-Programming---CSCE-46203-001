package com.example.degreeplanner.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

//reads instructor data from JSON and saves to room
class InstructorDataLoader {

    suspend fun loadInstructors(
        context: Context,
        instructorDao: InstructorDao
    ): Int = withContext(Dispatchers.IO) {

        //open instructor JSON file
        val jsonString = context.assets
            .open("CS_Degree_Planner_Instructors.json")
            .bufferedReader()
            .use { it.readText() }

        //convert JSON into list of instructors
        val jsonArray = JSONArray(jsonString)
        val instructors = mutableListOf<InstructorEntity>()

        for (i in 0 until jsonArray.length()) {

            val jsonInstructor = jsonArray.getJSONObject(i)

            //convert each JSON record into a room entity
            val instructor = InstructorEntity(
                instructorId = jsonInstructor.getString("instructorId"),
                name = jsonInstructor.getString("name"),
                department = jsonInstructor.getString("department"),
                email = getOptionalString(jsonInstructor, "email"),
                office = getOptionalString(jsonInstructor, "office")
            )

            instructors.add(instructor)
        }

        //save all instructors into room
        instructorDao.insertInstructors(instructors)

        //return number of records imported
        instructors.size
    }

    //handles optional info that may be null
    private fun getOptionalString(
        json: JSONObject,
        field: String
    ): String? {
        return if (json.has(field) && !json.isNull(field)) {
            json.getString(field)
        } else {
            null
        }
    }
}