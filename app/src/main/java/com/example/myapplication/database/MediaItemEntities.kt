package com.example.myapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,         // Название (Fallout: New Vegas)
    val type: String,          // Категория (Игра, Книга, Музыка)
    val rating: Double,        // Оценка (9.0)
    val universe: String?,     // Вселенная (Fallout)
    val genre: String?,        // Жанры через запятую (RPG, Action)
    val status: String,        // Статус (Прошел, Забросил)
    val completions: Int,
    val comment: String?,      // Твое экспертное мнение
    val collections: String?,  // Теги для топов (Любимое, Топ-2026)
    val timestamp: Long        // Дата добавления для сортировки

)


@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val rating: Double,
    val genre: String?,
    val platform: String?,
    val universe: String?,
    val author: String?,
    val status: String,
    val hoursPlayed: Int?,
    val completions: Int,
    val achievements: String?,
    val comment: String?,
    val collections: String?,
    val releaseYear: Int?,
    val timestamp: Long
)

// ============================================
// КНИГИ
// ============================================
@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val rating: Double,
    val genre: String?,
    val author: String?,
    val pages: Int?,
    val status: String,
    val completions: Int,
    val comment: String?,
    val collections: String?,
    val releaseYear: Int?,
    val timestamp: Long
)

// ============================================
// ФИЛЬМЫ
// ============================================
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val rating: Double,
    val genre: String?,
    val director: String?,
    val durationMinutes: Int?,
    val status: String,
    val completions: Int,
    val comment: String?,
    val collections: String?,
    val releaseYear: Int?,
    val timestamp: Long
)

// ============================================
// СЕРИАЛЫ
// ============================================
@Entity(tableName = "series")
data class Series(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val rating: Double,
    val genre: String?,
    val seasonsTotal: Int?,
    val seasonsWatched: Int?,
    val episodesWatched: Int?,
    val status: String,
    val completions: Int,
    val comment: String?,
    val collections: String?,
    val releaseYear: Int?,
    val timestamp: Long
)

// ============================================
// МУЗЫКА (альбомы)
// ============================================
@Entity(tableName = "music")
data class Music(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val rating: Double,
    val genre: String?,
    val artist: String?,
    val album: String?,
    val comment: String?,
    val collections: String?,
    val status: String,
    val releaseYear: Int?,
    val timestamp: Long
)