package com.example.degreeplanner.local

import androidx.room.TypeConverter
import org.json.JSONArray

//converts lists so room can save and retrieve them
class CourseConverters {

    //converts a list into a JSON string for storage
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return JSONArray(value).toString()
    }

    //converts a stored JSON string back into a list
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val jsonArray = JSONArray(value)

        return List(jsonArray.length()) { index ->
            jsonArray.getString(index)
        }
    }
}