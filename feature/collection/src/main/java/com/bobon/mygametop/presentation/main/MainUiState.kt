package com.bobon.mygametop.presentation.main

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.model.GameQuery

data class MainUiState(
    val games: List<Game> = emptyList(),
    val total: Int = 0,
    val averageRating: Double? = null,
    val genres: List<String> = emptyList(),
    val query: GameQuery = GameQuery(),
    val loading: Boolean = true
)
