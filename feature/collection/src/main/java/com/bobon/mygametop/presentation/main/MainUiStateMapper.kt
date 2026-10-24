package com.bobon.mygametop.presentation.main

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.model.GameQuery
import com.bobon.mygametop.domain.model.GameStatistics

internal fun createMainUiState(games: List<Game>, query: GameQuery): MainUiState {
    val visibleGames = query.apply(games)
    return MainUiState(
        games = visibleGames, total = games.size,
        averageRating = GameStatistics.from(visibleGames).averageRating,
        genres = games.flatMap { it.genres }.distinct().sorted(), query = query, loading = false
    )
}
