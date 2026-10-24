package com.bobon.mygametop.data.local.room

import androidx.room.TypeConverter
import org.json.JSONArray

object StringListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>): String = JSONArray(value).toString()

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val jsonArray = JSONArray(value)
        return List(jsonArray.length()) { index -> jsonArray.getString(index) }
    }
}
