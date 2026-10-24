package com.bobon.mygametop.presentation.add

import com.bobon.mygametop.domain.model.GameStatus

data class AddGameUiState(
    val title: String = "", val completions: Int = 0,
    val status: GameStatus = GameStatus.PLAYING, val rating: Float = 0f,
    val coverUri: String? = null, val completionDates: List<String> = emptyList(),
    val series: String = "", val universe: String = "", val genres: List<String> = emptyList(),
    val tags: List<String> = emptyList(), val developers: List<String> = emptyList(),
    val platforms: List<String> = emptyList(), val releaseYear: String = "",
    val opinion: String = "", val playtimeHours: String = "",
    val isLoading: Boolean = true, val loadFailed: Boolean = false, val isEditing: Boolean = false,
    val isSaving: Boolean = false, val saved: Boolean = false, val message: String? = null
)
