package com.bobon.mygametop.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val completions: Int,
    val status: String,
    val rating: Float,
    val coverUri: String? = null,
    val completionDates: List<String> = emptyList(),
    val series: String? = null,
    val universe: String? = null,
    val genres: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val developers: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
    val releaseYear: Int? = null,
    val opinion: String? = null,
    val playtimeHours: Double? = null
)
