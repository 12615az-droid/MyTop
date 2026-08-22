package com.bobon.mygametop.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.model.GameStatus
import com.bobon.mygametop.domain.usecase.AddGameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddGameUiState(
    val title: String = "", val completions: Int = 0,
    val status: GameStatus = GameStatus.PLAYING, val rating: Float = 0f,
    val coverUri: String? = null, val completionDates: List<String> = emptyList(),
    val series: String = "", val universe: String = "", val genres: List<String> = emptyList(),
    val tags: List<String> = emptyList(), val developers: List<String> = emptyList(),
    val platforms: List<String> = emptyList(), val releaseYear: String = "",
    val opinion: String = "", val playtimeHours: String = "",
    val isSaving: Boolean = false, val saved: Boolean = false, val message: String? = null
)

class AddGameViewModel(private val addGame: AddGameUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(AddGameUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChange(v: String) = update { copy(title = v) }
    fun onCompletionsChange(v: Int) = update { copy(completions = v.coerceAtLeast(0)) }
    fun onStatusChange(v: GameStatus) = update { copy(status = v) }
    fun onRatingChange(v: Float) = update { copy(rating = v.coerceIn(0f, 10f)) }
    fun onCoverUriChange(v: String?) = update { copy(coverUri = v) }
    fun addCompletionDate(v: String) = update { copy(completionDates = (completionDates + v).distinct().sorted()) }
    fun removeCompletionDate(v: String) = update { copy(completionDates = completionDates - v) }
    fun onSeriesChange(v: String) = update { copy(series = v) }
    fun onUniverseChange(v: String) = update { copy(universe = v) }
    fun onGenresChange(v: List<String>) = update { copy(genres = v) }
    fun onTagsChange(v: List<String>) = update { copy(tags = v) }
    fun onDevelopersChange(v: List<String>) = update { copy(developers = v) }
    fun onPlatformsChange(v: List<String>) = update { copy(platforms = v) }
    fun onReleaseYearChange(v: String) { if (v.length <= 4 && v.all(Char::isDigit)) update { copy(releaseYear = v) } }
    fun onOpinionChange(v: String) = update { copy(opinion = v) }
    fun onPlaytimeHoursChange(v: String) {
        val normalized = v.replace(',', '.')
        if (normalized.isEmpty() || normalized.matches(Regex("""\d{0,6}(\.\d{0,2})?"""))) update { copy(playtimeHours = v) }
    }

    fun save() {
        val state = _uiState.value
        if (state.title.isBlank()) { update { copy(message = "Для сохранения нужно название") }; return }
        if (state.isSaving) return
        viewModelScope.launch {
            update { copy(isSaving = true, message = null) }
            runCatching { addGame(state.toDomain()) }
                .onSuccess { update { copy(isSaving = false, saved = true) } }
                .onFailure { update { copy(isSaving = false, message = "Не удалось сохранить игру") } }
        }
    }

    fun consumeSaved() = update { copy(saved = false) }
    fun consumeMessage() = update { copy(message = null) }
    private inline fun update(transform: AddGameUiState.() -> AddGameUiState) { _uiState.value = _uiState.value.transform() }
}

private fun AddGameUiState.toDomain() = Game(
    title = title, completions = completions, status = status, rating = rating,
    coverUri = coverUri, completionDates = completionDates,
    series = series.takeIf { it.isNotBlank() }, universe = universe.takeIf { it.isNotBlank() },
    genres = genres, tags = tags, developers = developers, platforms = platforms,
    releaseYear = releaseYear.toIntOrNull(), opinion = opinion.takeIf { it.isNotBlank() },
    playtimeHours = playtimeHours.replace(',', '.').toDoubleOrNull()
)
