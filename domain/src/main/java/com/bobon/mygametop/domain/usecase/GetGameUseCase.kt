package com.bobon.mygametop.domain.usecase

import com.bobon.mygametop.domain.repository.GameRepository

class GetGameUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(id: Long) = repository.getGame(id)
}
