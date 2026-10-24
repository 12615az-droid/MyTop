package com.bobon.mygametop

import com.bobon.mygametop.domain.model.*
import com.bobon.mygametop.domain.repository.GameRepository
import com.bobon.mygametop.domain.usecase.UpdateGameUseCase
import com.bobon.mygametop.presentation.add.toDomain
import com.bobon.mygametop.presentation.add.toEditorState
import com.bobon.mygametop.data.transfer.GameText
import com.bobon.mygametop.domain.transfer.GameExportOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class GameEditingTest {
    private val original = Game(
        id = 42, title = "Portal", completions = 2, status = GameStatus.COMPLETED, rating = 9f,
        coverUri = "content://cover/1", completionDates = listOf("20.10.2026"), series = "Portal",
        universe = "Half-Life", genres = listOf("Головоломка"), tags = listOf("Любимое"),
        developers = listOf("Valve"), platforms = listOf("PC"), releaseYear = 2007,
        opinion = "Первая строка\nВторая строка", playtimeHours = 12.5
    )

    @Test fun editorPreservesEveryExistingField() {
        val state = original.toEditorState()
        assertTrue(state.isEditing)
        assertFalse(state.isLoading)
        assertEquals(original, state.toDomain().copy(id = original.id))
    }





    @Test fun textReportContainsReadableRankedGamesAndDetails() {
        val text = GameText.encode(GameExportOptions().select(listOf(original.copy(id = 43, title = "Другая игра", rating = 0f), original), GameQuery()))
        assertTrue(text.contains("Всего игр: 2"))
        assertTrue(text.contains("Средняя оценка: 4,5 / 10"))
        assertTrue(text.contains("1. Portal — 9,0 / 10"))
        assertTrue(text.contains("2. Другая игра — 0,0 / 10"))
        assertTrue(text.contains("Статус: Пройдено"))
        assertTrue(text.contains("Жанры: Головоломка"))
        assertTrue(text.contains("Вторая строка"))
        assertTrue(text.contains("Даты прохождений: 20.10.2026"))
    }

    @Test fun emptyTextReportHasNoNaN() {
        val text = GameText.encode(emptyList())
        assertTrue(text.contains("Средняя оценка: — / 10"))
        assertFalse(text.contains("NaN"))
    }
}
