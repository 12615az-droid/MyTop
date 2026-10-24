package com.bobon.mygametop.data.repository

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.data.local.room.*
import com.bobon.mygametop.data.mapper.toDomain
import com.bobon.mygametop.data.mapper.toEntity
import com.bobon.mygametop.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepositoryImpl(private val dao: GameDao) : GameRepository {
    override fun observeGames(): Flow<List<Game>> = dao.observeGames().map { list -> list.map(GameEntity::toDomain) }
    override suspend fun getGame(id: Long): Game? = dao.getGame(id)?.toDomain()
    override suspend fun updateGame(game: Game): Boolean = dao.updateGame(game.toEntity()) > 0
    override suspend fun deleteGame(id: Long): Boolean = dao.deleteGame(id) > 0
    override suspend fun importGames(games: List<Game>) = dao.importGames(games.map { it.toEntity() })
    override suspend fun addGame(game: Game): Long = dao.insertGame(game.toEntity())
}
