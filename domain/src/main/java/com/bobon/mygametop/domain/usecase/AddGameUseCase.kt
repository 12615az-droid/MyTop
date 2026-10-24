package com.bobon.mygametop.domain.usecase

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.repository.GameRepository

class AddGameUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(game: Game): Long = repository.addGame(game.validated())
}
