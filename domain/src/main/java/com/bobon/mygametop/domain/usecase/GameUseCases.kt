package com.bobon.mygametop.domain.usecase

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class ObserveGamesUseCase(private val repository: GameRepository) {
    operator fun invoke(): Flow<List<Game>> = repository.observeGames()
}

class AddGameUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(game: Game): Long {
        require(game.title.isNotBlank()) { "Game title must not be blank" }
        require(game.completions >= 0) { "Completions must be non-negative" }
        require(game.rating in 0f..10f) { "Rating must be between 0 and 10" }
        return repository.addGame(game.copy(
            title = game.title.trim(),
            series = game.series?.trim()?.takeIf { it.isNotEmpty() },
            universe = game.universe?.trim()?.takeIf { it.isNotEmpty() },
            opinion = game.opinion?.trim()?.takeIf { it.isNotEmpty() }
        ))
    }
}
