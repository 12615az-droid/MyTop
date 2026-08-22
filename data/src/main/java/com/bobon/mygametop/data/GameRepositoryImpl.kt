package com.bobon.mygametop.data

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.model.GameStatus
import com.bobon.mygametop.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepositoryImpl(private val dao: GameDao) : GameRepository {
    override fun observeGames(): Flow<List<Game>> = dao.observeGames().map { list -> list.map(GameEntity::toDomain) }
    override suspend fun addGame(game: Game): Long = dao.insertGame(game.toEntity())
}

private fun GameEntity.toDomain() = Game(
    id = id, title = title, completions = completions,
    status = runCatching { GameStatus.valueOf(status) }.getOrDefault(GameStatus.PLAYING),
    rating = rating, coverUri = coverUri, completionDates = completionDates,
    series = series, universe = universe, genres = genres, tags = tags,
    developers = developers, platforms = platforms, releaseYear = releaseYear,
    opinion = opinion, playtimeHours = playtimeHours
)

private fun Game.toEntity() = GameEntity(
    id = id, title = title, completions = completions, status = status.name,
    rating = rating, coverUri = coverUri, completionDates = completionDates,
    series = series, universe = universe, genres = genres, tags = tags,
    developers = developers, platforms = platforms, releaseYear = releaseYear,
    opinion = opinion, playtimeHours = playtimeHours
)
