package com.bobon.mygametop

import com.bobon.mygametop.domain.model.*
import com.bobon.mygametop.domain.repository.GameRepository
import com.bobon.mygametop.domain.usecase.UpdateGameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class GameUseCasesTest {
    private val original = Game(
        id = 42, title = "Portal", completions = 2, status = GameStatus.COMPLETED, rating = 9f,
        coverUri = "content://cover/1", completionDates = listOf("20.10.2026"), series = "Portal",
        universe = "Half-Life", genres = listOf("Головоломка"), tags = listOf("Любимое"),
        developers = listOf("Valve"), platforms = listOf("PC"), releaseYear = 2007,
        opinion = "Первая строка\nВторая строка", playtimeHours = 12.5
    )


    @Test fun updateChangesSameRecordWithoutAddingAnother() = runBlocking {
        val repository = MemoryRepository(listOf(original))
        UpdateGameUseCase(repository)(original.copy(title = " Portal 2 ", rating = 10f))
        assertEquals(1, repository.games.value.size)
        assertEquals(original.copy(title = "Portal 2", rating = 10f), repository.getGame(42))
        assertEquals(0, repository.addCalls)
    }

    @Test fun updatingDeletedRecordDoesNotRecreateIt() = runBlocking {
        val repository = MemoryRepository(emptyList())
        try {
            UpdateGameUseCase(repository)(original)
            fail("Updating a missing game must fail")
        } catch (_: IllegalStateException) { }
        assertTrue(repository.games.value.isEmpty())
        assertEquals(0, repository.addCalls)
    }

    @Test fun invalidEditLeavesStoredGameUnchanged() = runBlocking {
        val repository = MemoryRepository(listOf(original))
        try {
            UpdateGameUseCase(repository)(original.copy(rating = 11f))
            fail("Invalid rating must fail")
        } catch (_: IllegalArgumentException) { }
        assertEquals(original, repository.getGame(42))
    }

    @Test fun averageIncludesZeroRatingsAndEmptyCollectionHasNoAverage() {
        assertNull(GameStatistics.from(emptyList()).averageRating)
        val statistics = GameStatistics.from(listOf(original, original.copy(id = 43, rating = 0f)))
        assertEquals(2, statistics.total)
        assertEquals(4.5, statistics.averageRating!!, 0.00001)
    }
}

private class MemoryRepository(initial: List<Game>) : GameRepository {
    val games = MutableStateFlow(initial)
    var addCalls = 0
    override fun observeGames() = games
    override suspend fun getGame(id: Long) = games.value.find { it.id == id }
    override suspend fun updateGame(game: Game): Boolean {
        if (getGame(game.id) == null) return false
        games.value = games.value.map { if (it.id == game.id) game else it }
        return true
    }
    override suspend fun deleteGame(id: Long): Boolean {
        if (getGame(id) == null) return false
        games.value = games.value.filterNot { it.id == id }
        return true
    }
    override suspend fun addGame(game: Game): Long { addCalls++; return game.id }
    override suspend fun importGames(games: List<Game>) { this.games.value += games }
}
