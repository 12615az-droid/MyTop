package com.bobon.mygametop.domain.repository

import com.bobon.mygametop.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun observeGames(): Flow<List<Game>>
    suspend fun getGame(id: Long): Game?
    suspend fun updateGame(game: Game): Boolean
    suspend fun deleteGame(id: Long): Boolean
    suspend fun addGame(game: Game): Long
    suspend fun importGames(games: List<Game>)
}
