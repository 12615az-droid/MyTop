package com.bobon.mygametop.data

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [
        GameEntity::class
    ],
    version = 1,
    exportSchema = true
)
@ColumnTypeConverters(
    StringListConverter::class
)
abstract class MyTopDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
}