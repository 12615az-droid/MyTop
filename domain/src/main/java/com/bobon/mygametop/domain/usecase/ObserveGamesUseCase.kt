package com.bobon.mygametop.domain.usecase

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class ObserveGamesUseCase(private val repository: GameRepository) {
    operator fun invoke(): Flow<List<Game>> = repository.observeGames()
}
