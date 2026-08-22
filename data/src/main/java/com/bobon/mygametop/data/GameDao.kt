package com.bobon.mygametop.data


import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query(
        """
        SELECT * 
        FROM games
        ORDER BY title COLLATE NOCASE ASC
        """
    )
    fun observeGames(): Flow<List<GameEntity>>

    @Query(
        """
        SELECT *
        FROM games
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getGameById(id: Long): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    @Update
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGameById(id: Long)
}