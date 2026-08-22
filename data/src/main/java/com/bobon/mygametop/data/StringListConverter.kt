package com.bobon.mygametop.data


import androidx.room3.ColumnTypeConverter
import org.json.JSONArray

object StringListConverter {

    @ColumnTypeConverter
    fun fromStringList(value: List<String>): String {
        return JSONArray(value).toString()
    }

    @ColumnTypeConverter
    fun toStringList(value: String): List<String> {
        val jsonArray = JSONArray(value)

        return List(jsonArray.length()) { index ->
            jsonArray.getString(index)
        }
    }
}