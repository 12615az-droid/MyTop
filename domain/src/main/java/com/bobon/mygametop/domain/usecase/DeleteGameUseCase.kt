package com.bobon.mygametop.domain.usecase

import com.bobon.mygametop.domain.repository.GameRepository

class DeleteGameUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(id: Long): Boolean = repository.deleteGame(id)
}
