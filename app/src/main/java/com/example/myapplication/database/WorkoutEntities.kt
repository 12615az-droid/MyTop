package com.bobon.mypace.database

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


