package com.bobon.mygametop.presentation.add

import com.bobon.mygametop.domain.model.Game

internal fun AddGameUiState.toDomain() = Game(
    title = title, completions = completions, status = status, rating = rating,
    coverUri = coverUri, completionDates = completionDates,
    series = series.takeIf { it.isNotBlank() }, universe = universe.takeIf { it.isNotBlank() },
    genres = genres, tags = tags, developers = developers, platforms = platforms,
    releaseYear = releaseYear.toIntOrNull(), opinion = opinion.takeIf { it.isNotBlank() },
    playtimeHours = playtimeHours.replace(',', '.').toDoubleOrNull()
)

internal fun Game.toEditorState() = AddGameUiState(
    title = title, completions = completions, status = status, rating = rating,
    coverUri = coverUri, completionDates = completionDates, series = series.orEmpty(),
    universe = universe.orEmpty(), genres = genres, tags = tags, developers = developers,
    platforms = platforms, releaseYear = releaseYear?.toString().orEmpty(), opinion = opinion.orEmpty(),
    playtimeHours = playtimeHours?.toString().orEmpty(), isLoading = false, isEditing = true
)
