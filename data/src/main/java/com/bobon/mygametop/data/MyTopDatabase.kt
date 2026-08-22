package com.bobon.mygametop.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class MyTopDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
