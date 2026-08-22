package com.bobon.mygametop.domain.repository

import com.bobon.mygametop.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun observeGames(): Flow<List<Game>>
    suspend fun addGame(game: Game): Long
    // Future filtering/sorting should enter through query objects/use cases here.
}
