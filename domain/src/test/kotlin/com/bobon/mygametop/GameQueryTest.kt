package com.bobon.mygametop

import com.bobon.mygametop.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class GameQueryTest {
    private fun game(title: String = "Игра", rating: Float = 8f, id: Long = 1) = Game(
        id = id, title = title, completions = 1, status = GameStatus.COMPLETED, rating = rating
    )

    @Test fun filtersCombineAndSearchIsCaseInsensitive() {
        val games = listOf(
            game("A").copy(genres = listOf("RPG"), platforms = listOf("Steam Deck")),
            game("B", 9f).copy(status = GameStatus.DROPPED, genres = listOf("RPG")),
            game("C", 6f).copy(genres = listOf("Puzzle"))
        )
        val query = GameQuery(search = " deck ", status = GameStatus.COMPLETED, genre = "rpg", minimumRating = 7f)
        assertEquals(listOf(games[0]), query.apply(games))
        assertTrue(query.copy(minimumRating = 9f).apply(games).isEmpty())
    }

    @Test fun sortingIsDeterministicAndMissingYearIsLast() {
        val games = listOf(game("B", 8f, 2), game("A", 8f, 3), game("C", 9f, 1))
        assertEquals(listOf("C", "A", "B"), GameQuery().apply(games).map { it.title })
        assertEquals(listOf("A", "B", "C"), GameQuery(sort = GameSort.TITLE).apply(games).map { it.title })
        assertEquals(listOf(3L, 2L, 1L), GameQuery(sort = GameSort.NEWEST).apply(games).map { it.id })
        val dated = listOf(games[0], games[1].copy(releaseYear = 2020), games[2].copy(releaseYear = 2025))
        assertEquals(listOf("C", "A", "B"), GameQuery(sort = GameSort.RELEASE_YEAR).apply(dated).map { it.title })
    }
}
