package com.bobon.mygametop.presentation.add

import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.AppStrings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobon.mygametop.domain.model.GameStatus
import com.bobon.mygametop.domain.usecase.AddGameUseCase
import com.bobon.mygametop.domain.usecase.UpdateGameUseCase
import com.bobon.mygametop.domain.usecase.GetGameUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AddGameViewModel(
    private val addGame: AddGameUseCase,
    private val updateGame: UpdateGameUseCase,
    private val getGame: GetGameUseCase,
    private val strings: AppStrings
) : ViewModel() {
    private var initialized = false
    private var editingId: Long? = null
    private val _uiState = MutableStateFlow(AddGameUiState())
    val uiState = _uiState.asStateFlow()

    fun initialize(gameId: Long?) {
        if (initialized) return
        initialized = true
        editingId = gameId
        if (gameId == null) {
            update { copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            try {
                val game = getGame(gameId) ?: error(strings.get(R.string.game_not_found))
                _uiState.value = game.toEditorState()
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) {
                update { copy(isLoading = false, loadFailed = true, isEditing = true, message = strings.get(R.string.load_game_error)) }
            }
        }
    }

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
        if (state.isLoading || state.loadFailed || state.isSaving) return
        if (state.title.isBlank()) { update { copy(message = strings.get(R.string.title_needed)) }; return }
        viewModelScope.launch {
            update { copy(isSaving = true, message = null) }
            try {
                val game = state.toDomain().copy(id = editingId ?: 0)
                if (editingId == null) addGame(game) else updateGame(game)
                update { copy(isSaving = false, saved = true) }
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { update { copy(isSaving = false, message = strings.get(R.string.save_game_error)) } }
        }
    }

    fun consumeSaved() = update { copy(saved = false) }
    fun consumeMessage() = update { copy(message = null) }
    private inline fun update(transform: AddGameUiState.() -> AddGameUiState) { _uiState.value = _uiState.value.transform() }
}
