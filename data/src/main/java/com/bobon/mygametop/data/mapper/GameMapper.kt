package com.bobon.mygametop.data.mapper

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.model.GameStatus
import com.bobon.mygametop.data.local.room.GameEntity

internal fun GameEntity.toDomain() = Game(
    id = id, title = title, completions = completions,
    status = runCatching { GameStatus.valueOf(status) }.getOrDefault(GameStatus.PLAYING),
    rating = rating, coverUri = coverUri, completionDates = completionDates,
    series = series, universe = universe, genres = genres, tags = tags,
    developers = developers, platforms = platforms, releaseYear = releaseYear,
    opinion = opinion, playtimeHours = playtimeHours
)

internal fun Game.toEntity() = GameEntity(
    id = id, title = title, completions = completions, status = status.name,
    rating = rating, coverUri = coverUri, completionDates = completionDates,
    series = series, universe = universe, genres = genres, tags = tags,
    developers = developers, platforms = platforms, releaseYear = releaseYear,
    opinion = opinion, playtimeHours = playtimeHours
)
