package com.bobon.mygametop

import com.bobon.mygametop.domain.model.*
import com.bobon.mygametop.data.transfer.GameJson
import org.junit.Assert.*
import org.junit.Test

class GameCollectionTest {
    private fun game(title: String = "Игра", rating: Float = 8f, id: Long = 1) = Game(
        id = id, title = title, completions = 1, status = GameStatus.COMPLETED, rating = rating
    )

    @Test fun jsonRoundTripPreservesEveryFieldButResetsDatabaseId() {
        val original = game().copy(
            coverUri = "content://covers/1", completionDates = listOf("20.10.2026"), series = "Серия",
            universe = "Мир", genres = listOf("RPG"), tags = listOf("Любимое"), developers = listOf("Studio"),
            platforms = listOf("PC"), releaseYear = 2025, opinion = "Отлично!\nЕщё раз", playtimeHours = 42.5
        )
        assertEquals(listOf(original.copy(id = 0)), GameJson.decode(GameJson.encode(listOf(original))))
    }

    @Test fun acceptsPlainArrayAndOptionalDefaults() {
        val decoded = GameJson.decode("""[{"title":" Portal ","rating":9.5,"completions":2,"status":"COMPLETED"}]""")
        assertEquals("Portal", decoded.single().title)
        assertTrue(decoded.single().genres.isEmpty())
    }

    @Test fun rejectsWholeFileWhenOneRatingIsInvalid() {
        val text = GameJson.encode(listOf(game(), game("Bad", 11f)))
        assertThrows(IllegalArgumentException::class.java) { GameJson.decode(text) }
    }

    @Test fun rejectsUnknownVersionStatusAndBlankTitle() {
        assertThrows(IllegalArgumentException::class.java) { GameJson.decode("""{"version":2,"games":[]}""") }
        assertThrows(IllegalArgumentException::class.java) {
            GameJson.decode("""[{"title":"Game","rating":8,"completions":0,"status":"UNKNOWN"}]""")
        }
        assertThrows(IllegalArgumentException::class.java) { GameJson.decode(GameJson.encode(listOf(game(" ")))) }
    }



    @Test fun emptyCollectionRoundTrips() {
        assertTrue(GameJson.decode(GameJson.encode(emptyList())).isEmpty())
    }
}
