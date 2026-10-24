package com.bobon.mygametop.domain.model

enum class GameSort { RATING_DESC, RATING_ASC, TITLE, NEWEST, RELEASE_YEAR }

data class GameQuery(
    val search: String = "",
    val status: GameStatus? = null,
    val genre: String? = null,
    val minimumRating: Float = 0f,
    val sort: GameSort = GameSort.RATING_DESC
) {
    fun apply(games: List<Game>): List<Game> {
        val words = search.searchWords()
        val filtered = games.filter { game ->
            val searchable = (listOf(game.title, game.series.orEmpty(), game.universe.orEmpty()) +
                game.tags + game.genres + game.developers + game.platforms).joinToString(" ").searchWords()
            words.all { word -> searchable.any { it.contains(word, ignoreCase = true) } } &&
                (status == null || game.status == status) &&
                (genre == null || game.genres.any { it.equals(genre, ignoreCase = true) }) &&
                game.rating >= minimumRating
        }
        val comparator = when (sort) {
            GameSort.RATING_DESC -> compareByDescending<Game> { it.rating }
            GameSort.RATING_ASC -> compareBy<Game> { it.rating }
            GameSort.TITLE -> compareBy<Game> { it.title.lowercase(java.util.Locale.ROOT) }
            GameSort.NEWEST -> compareByDescending<Game> { it.id }
            GameSort.RELEASE_YEAR -> compareByDescending<Game> { it.releaseYear ?: Int.MIN_VALUE }
        }
        return filtered.sortedWith(comparator.thenBy { it.title.lowercase(java.util.Locale.ROOT) }.thenBy { it.id })
    }
}

private val searchWhitespace = Regex("[\\s\\p{Z}]+")
private fun String.searchWords(): List<String> = split(searchWhitespace).filter { it.isNotEmpty() }
