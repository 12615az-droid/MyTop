package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(
    entities = [Game::class, Book::class, Movie::class, Series::class, Music::class],
    version = 1,
    exportSchema = false
)
abstract class MediaItemDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
    abstract fun bookDao(): BookDao
    abstract fun movieDao(): MovieDao
    abstract fun seriesDao(): SeriesDao
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: MediaItemDatabase? = null

        fun getDatabase(context: Context): MediaItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediaItemDatabase::class.java,
                    "media_item_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
