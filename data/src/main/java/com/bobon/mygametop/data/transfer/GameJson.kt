package com.bobon.mygametop.data.transfer

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.model.GameStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray

@Serializable
private data class GameDocument(val version: Int = 1, val games: List<GameRecord>)

@Serializable
private data class GameRecord(
    val id: Long = 0,
    val title: String,
    val completions: Int,
    val status: String,
    val rating: Float,
    val coverUri: String? = null,
    val completionDates: List<String> = emptyList(),
    val series: String? = null,
    val universe: String? = null,
    val genres: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val developers: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
    val releaseYear: Int? = null,
    val opinion: String? = null,
    val playtimeHours: Double? = null
) {
    fun toGame(): Game = Game(
        id = id,
        title = title,
        completions = completions,
        status = GameStatus.valueOf(status),
        rating = rating,
        coverUri = coverUri,
        completionDates = completionDates,
        series = series,
        universe = universe,
        genres = genres,
        tags = tags,
        developers = developers,
        platforms = platforms,
        releaseYear = releaseYear,
        opinion = opinion,
        playtimeHours = playtimeHours
    )

    companion object {
        fun from(game: Game) = with(game) {
            GameRecord(
                id = id,
                title = title,
                completions = completions,
                status = status.name,
                rating = rating,
                coverUri = coverUri,
                completionDates = completionDates,
                series = series,
                universe = universe,
                genres = genres,
                tags = tags,
                developers = developers,
                platforms = platforms,
                releaseYear = releaseYear,
                opinion = opinion,
                playtimeHours = playtimeHours
            )
        }
    }
}

object GameJson {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true; encodeDefaults = true }

    fun encode(games: List<Game>, omittedFields: Set<String> = emptySet()): String {
        val document = json.encodeToJsonElement(GameDocument(games = games.map(GameRecord::from))).jsonObject
        val records = document.getValue("games").jsonArray.map { record ->
            JsonObject(record.jsonObject.filterKeys { it !in omittedFields })
        }
        return json.encodeToString(JsonObject.serializer(), JsonObject(document + ("games" to JsonArray(records))))
    }

    fun decode(text: String): List<Game> {
        val root = json.parseToJsonElement(text)
        val records = if (root is JsonArray) {
            json.decodeFromJsonElement(kotlinx.serialization.builtins.ListSerializer(GameRecord.serializer()), root)
        } else {
            val document = json.decodeFromJsonElement(GameDocument.serializer(), root)
            require(document.version == 1) { "Неподдерживаемая версия JSON" }
            document.games
        }
        require(records.size <= 10_000) { "В файле больше 10 000 игр" }
        return records.map { record ->
            val game = record.toGame()
            require(game.title.isNotBlank()) { "У игры отсутствует название" }
            require(game.rating.isFinite() && game.rating in 0f..10f) { "Оценка должна быть от 0 до 10" }
            require(game.completions >= 0) { "Число прохождений не может быть отрицательным" }
            val hours = game.playtimeHours
            require(hours == null || (hours.isFinite() && hours >= 0)) {
                "Некорректное время игры"
            }
            game.copy(id = 0, title = game.title.trim())
        }
    }
}
