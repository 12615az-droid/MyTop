package com.bobon.mygametop.domain.model

data class GameStatistics(val total: Int, val averageRating: Double?) {
    companion object {
        fun from(games: List<Game>) = GameStatistics(
            total = games.size,
            averageRating = if (games.isEmpty()) null else games.map { it.rating.toDouble() }.average()
        )
    }
}
