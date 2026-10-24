package com.bobon.mygametop.domain.usecase

import com.bobon.mygametop.domain.model.Game

internal fun Game.validated(): Game {
    require(title.isNotBlank()) { "Game title must not be blank" }
    require(completions >= 0) { "Completions must be non-negative" }
    require(rating.isFinite() && rating in 0f..10f) { "Rating must be between 0 and 10" }
    val hours = playtimeHours
    require(hours == null || (hours.isFinite() && hours >= 0)) { "Invalid playtime" }
    return copy(
        title = title.trim(),
        series = series?.trim()?.takeIf { it.isNotEmpty() },
        universe = universe?.trim()?.takeIf { it.isNotEmpty() },
        opinion = opinion?.trim()?.takeIf { it.isNotEmpty() }
    )
}
