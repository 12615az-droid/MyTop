package com.bobon.mygametop

import com.bobon.mygametop.domain.model.*
import com.bobon.mygametop.presentation.main.createMainUiState
import com.bobon.mygametop.presentation.main.importExample
import com.bobon.mygametop.domain.transfer.*
import com.bobon.mygametop.data.transfer.*
import org.junit.Assert.*
import org.junit.Test

class GameTransferSettingsTest {
    private val portal = Game(
        id = 1, title = " Portal   2 ", rating = 10f, completions = 1, status = GameStatus.COMPLETED,
        genres = listOf("Puzzle"), platforms = listOf("PC"), releaseYear = 2011, series = "Portal",
        completionDates = listOf("20.10.2026"), playtimeHours = 12.5, opinion = "Superb", coverUri = "content://cover/1"
    )
    private val other = Game(id = 2, title = "Other", rating = 4f, completions = 0, status = GameStatus.PLAYING)

    @Test fun searchIgnoresExtraWhitespaceInQueryAndData() {
        listOf(" portal 2 ", "Portal   2", "PORTAL\t2", "Portal\n2", "Portal\u00a0\u00a02").forEach { search ->
            assertEquals(listOf(portal), GameQuery(search = search).apply(listOf(portal, other)))
        }
        assertEquals(2, GameQuery(search = " \t\n\u00a0 ").apply(listOf(portal, other)).size)
    }

    @Test fun searchCanCombineWordsFromDifferentFields() {
        assertEquals(listOf(portal), GameQuery(search = "portal   PC").apply(listOf(portal, other)))
        assertTrue(GameQuery(search = "portal xbox").apply(listOf(portal, other)).isEmpty())
    }

    @Test fun screenAverageFollowsStatusGenreMinimumAndSearch() {
        val games = listOf(portal, other)
        assertEquals(7.0, createMainUiState(games, GameQuery()).averageRating!!, 0.00001)
        listOf(GameQuery(status = GameStatus.COMPLETED), GameQuery(genre = "Puzzle"),
            GameQuery(minimumRating = 8f), GameQuery(search = "portal")).forEach { query ->
            val state = createMainUiState(games, query)
            assertEquals(10.0, state.averageRating!!, 0.00001)
            assertEquals(2, state.total)
            assertEquals(listOf(portal), state.games)
        }
        assertNull(createMainUiState(games, GameQuery(search = "no match")).averageRating)
    }

    @Test fun exportScopeUsesQueryOnlyForFilteredSelection() {
        val query = GameQuery(search = "portal", status = GameStatus.COMPLETED)
        assertEquals(listOf(portal), GameExportOptions(scope = GameExportScope.FILTERED).select(listOf(portal, other), query))
        assertEquals(2, GameExportOptions().select(listOf(portal, other), query).size)
        assertEquals(listOf(other, portal), GameExportOptions().select(listOf(portal, other), GameQuery(sort = GameSort.RATING_ASC)))
    }

    @Test fun omittedJsonFieldsAreAbsentAndResultCanBeImported() {
        val options = GameExportOptions(includeMetadata = false, includeHistory = false, includeOpinion = false, includeCover = false)
        val selected = options.select(listOf(portal), GameQuery())
        val json = GameJson.encode(selected, options.omittedFields)
        options.omittedFields.forEach { field -> assertFalse("Unexpected field $field", json.contains("\"$field\"")) }
        val restored = GameJson.decode(json).single()
        assertEquals("Portal   2", restored.title)
        assertEquals(10f, restored.rating)
        assertEquals(GameStatus.COMPLETED, restored.status)
        assertEquals(1, restored.completions)
        assertTrue(restored.genres.isEmpty())
        assertNull(restored.opinion)
        assertNull(restored.coverUri)
    }

    @Test fun textExportRespectsFieldChoicesAndSelectionStatistics() {
        val options = GameExportOptions(format = GameExportFormat.TEXT, scope = GameExportScope.FILTERED,
            includeMetadata = false, includeHistory = false, includeOpinion = false, includeCover = false)
        val selected = options.select(listOf(portal, other), GameQuery(search = "portal"))
        val text = GameText.encode(selected, options)
        assertTrue(text.contains("Всего игр: 1"))
        assertTrue(text.contains("Средняя оценка: 10,0 / 10"))
        listOf("Puzzle", "content://", "Superb", "2011", "12.5", "Даты прохождений").forEach { assertFalse(text.contains(it)) }
        assertTrue(text.contains("Статус: Пройдено"))
    }

    @Test fun topOnlyExportContainsOnlyPositionTitleAndRating() {
        val text = GameText.encodeTop(listOf(portal, other))
        assertEquals("1.  Portal   2  — 10,0 / 10\n2. Other — 4,0 / 10", text)
        listOf("Статус", "Всего игр", "Средняя оценка", "Puzzle", "COMPLETED").forEach {
            assertFalse(text.contains(it))
        }
    }

    @Test fun displayedImportExampleIsAValidImportFile() {
        val game = GameJson.decode(importExample).single()
        assertEquals("Portal 2", game.title)
        assertEquals(9.5f, game.rating)
    }
}
