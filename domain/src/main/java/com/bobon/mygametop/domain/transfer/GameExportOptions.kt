package com.bobon.mygametop.domain.transfer

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.model.GameQuery

enum class GameExportFormat { JSON, TEXT }
enum class GameExportScope { ALL, FILTERED }
enum class GameExportContent { FULL, TOP_ONLY }

data class GameExportOptions(
    val format: GameExportFormat = GameExportFormat.JSON,
    val scope: GameExportScope = GameExportScope.ALL,
    val includeMetadata: Boolean = true,
    val includeHistory: Boolean = true,
    val includeOpinion: Boolean = true,
    val includeCover: Boolean = true,
    val content: GameExportContent = GameExportContent.FULL
) {
    fun select(games: List<Game>, query: GameQuery): List<Game> {
        val selected = if (scope == GameExportScope.FILTERED) query.apply(games) else query.copy(
            search = "", status = null, genre = null, minimumRating = 0f
        ).apply(games)
        return selected.map { game ->
            game.copy(
                series = game.series.takeIf { includeMetadata }, universe = game.universe.takeIf { includeMetadata },
                genres = if (includeMetadata) game.genres else emptyList(),
                tags = if (includeMetadata) game.tags else emptyList(),
                developers = if (includeMetadata) game.developers else emptyList(),
                platforms = if (includeMetadata) game.platforms else emptyList(),
                releaseYear = game.releaseYear.takeIf { includeMetadata },
                completionDates = if (includeHistory) game.completionDates else emptyList(),
                playtimeHours = game.playtimeHours.takeIf { includeHistory },
                opinion = game.opinion.takeIf { includeOpinion }, coverUri = game.coverUri.takeIf { includeCover }
            )
        }
    }
}

data class GameExportRequest(val options: GameExportOptions, val query: GameQuery)
