package com.bobon.mygametop.domain.usecase

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.repository.GameRepository

class UpdateGameUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(game: Game) {
        require(game.id > 0) { "Game ID is required" }
        check(repository.updateGame(game.validated())) { "Игра уже удалена" }
    }
}
