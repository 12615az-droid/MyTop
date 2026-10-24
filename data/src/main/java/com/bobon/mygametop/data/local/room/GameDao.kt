package com.bobon.mygametop.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY id DESC")
    fun observeGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGame(id: Long): GameEntity?

    @Update
    suspend fun updateGame(game: GameEntity): Int

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGame(id: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGames(games: List<GameEntity>)

    @Transaction
    suspend fun importGames(games: List<GameEntity>) {
        insertGames(games.map { it.copy(id = 0) })
    }
}
