package com.bobon.mypace.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface WorkoutDao {

    @Insert
    suspend fun insertWorkout(workout: MediaItem): Long


}