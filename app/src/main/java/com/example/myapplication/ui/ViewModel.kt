package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainRepository
import com.example.myapplication.database.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.round

class ViewModel(
    private val repository: MainRepository
) : ViewModel() {

    // Состояния для полей ввода
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _completions = MutableStateFlow(1)
    val completions = _completions.asStateFlow()

    private val _type = MutableStateFlow("Игра")
    val type = _type.asStateFlow()

    private val _status = MutableStateFlow("В процессе")
    val status = _status.asStateFlow()

    private val _rating = MutableStateFlow(8.5f)
    val rating = _rating.asStateFlow()

    private val _universe = MutableStateFlow("")
    val universe = _universe.asStateFlow()

    private val _genre = MutableStateFlow("")
    val genre = _genre.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment = _comment.asStateFlow()

    private val _customTag = MutableStateFlow("")
    val customTag = _customTag.asStateFlow()

    // Методы обновления состояний
    fun updateTitle(newValue: String) { _title.value = newValue }
    fun updateCompletions(newValue: Int) { _completions.value = newValue }
    fun updateType(newValue: String) { _type.value = newValue }
    fun updateStatus(newValue: String) { _status.value = newValue }
    fun updateRating(newValue: Float) { _rating.value = newValue }
    fun updateUniverse(newValue: String) { _universe.value = newValue }
    fun updateGenre(newValue: String) { _genre.value = newValue }
    fun updateComment(newValue: String) { _comment.value = newValue }
    fun updateCustomTag(newValue: String) { _customTag.value = newValue }

    /**
     * Выход из экрана (назад)
     */
    fun onBackClicked() {
        // TODO: Реализовать навигацию назад
    }

    /**
     * Логика для кнопки добавления (если нужно отдельное действие)
     */
    fun onAddClicked() {
        // Очистка полей при необходимости
        _title.value = ""
        _universe.value = ""
        _comment.value = ""
    }

    /**
     * Сохранение данных в БД через репозиторий
     */
    fun onSaveClicked() {
        viewModelScope.launch {
            // Округляем до 1 знака после запятой, чтобы избежать 9.013626363...
            val roundedRating = round(_rating.value * 10) / 10.0
            
            val item = MediaItem(
                title = _title.value,
                type = _type.value,
                rating = roundedRating,
                universe = _universe.value,
                genre = _genre.value,
                status = _status.value,
                completions = _completions.value,
                comment = _comment.value,
                collections = _customTag.value,
                timestamp = System.currentTimeMillis()
            )

            
            // Здесь можно добавить закрытие экрана или уведомление об успехе
        }
    }
}
