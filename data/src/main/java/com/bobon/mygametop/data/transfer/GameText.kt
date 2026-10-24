package com.bobon.mygametop.data.transfer

import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.model.GameStatistics
import com.bobon.mygametop.domain.model.GameStatus
import java.util.Locale
import com.bobon.mygametop.domain.transfer.ExportLabel
import com.bobon.mygametop.domain.transfer.GameExportOptions

/** Human-readable UTF-8 report. JSON remains the format for restoring a collection. */
object GameText {
    fun encodeTop(games: List<Game>, locale: Locale = Locale.forLanguageTag("ru")): String = buildString {
        games.forEachIndexed { index, game ->
            appendLine("${index + 1}. ${game.title} — ${rating(game.rating.toDouble(), locale)} / 10")
        }
    }.trimEnd()

    fun encode(games: List<Game>, options: GameExportOptions = GameExportOptions(),
        text: (ExportLabel) -> String = ::defaultLabel, locale: Locale = Locale.forLanguageTag("ru")): String = buildString {
        val statistics = GameStatistics.from(games)
        appendLine(text(ExportLabel.app_name).uppercase(locale))
        appendLine("${text(ExportLabel.total_games)}: ${statistics.total}")
        appendLine("${text(ExportLabel.average_rating)}: ${statistics.averageRating?.let { rating(it, locale) } ?: "—"} / 10")
        if (games.isEmpty()) appendLine(text(ExportLabel.empty_collection))
        games.forEachIndexed { index, game ->
            appendLine()
            appendLine("${index + 1}. ${game.title} — ${rating(game.rating.toDouble(), locale)} / 10")
            appendLine("${text(ExportLabel.status)}: ${text(game.status.labelId())}")
            appendLine("${text(ExportLabel.completions)}: ${game.completions}")
            fun field(label: String, value: String?) {
                if (!value.isNullOrBlank()) appendLine("$label: $value")
            }
            if (options.includeMetadata) {
                field(text(ExportLabel.series), game.series)
                field(text(ExportLabel.universe), game.universe)
                field(text(ExportLabel.release_year), game.releaseYear?.toString())
                field(text(ExportLabel.genres), game.genres.joinToString())
                field(text(ExportLabel.platforms), game.platforms.joinToString())
                field(text(ExportLabel.developers), game.developers.joinToString())
                field(text(ExportLabel.tags), game.tags.joinToString())
            }
            if (options.includeHistory) {
                field(text(ExportLabel.completion_dates), game.completionDates.joinToString())
                field(text(ExportLabel.playtime), game.playtimeHours?.let { "$it ${text(ExportLabel.hours)}" })
            }
            if (options.includeOpinion) field(text(ExportLabel.opinion), game.opinion)
            if (options.includeCover) field(text(ExportLabel.cover_link), game.coverUri)
        }
    }

    private fun rating(value: Double, locale: Locale) = String.format(locale, "%.1f", value)
    private fun GameStatus.labelId() = when (this) {
        GameStatus.PLAYING -> ExportLabel.playing
        GameStatus.COMPLETED -> ExportLabel.completed
        GameStatus.REPLAYING -> ExportLabel.replaying
        GameStatus.DROPPED -> ExportLabel.dropped
    }
    // Pure encoder defaults; Android exports supply translated resources.
    private fun defaultLabel(id: ExportLabel): String = when (id) {
        ExportLabel.app_name -> "Мой топ игр"
        ExportLabel.total_games -> "Всего игр"
        ExportLabel.average_rating -> "Средняя оценка"
        ExportLabel.empty_collection -> "Коллекция пока пуста."
        ExportLabel.status -> "Статус"
        ExportLabel.completions -> "Прохождений"
        ExportLabel.series -> "Серия"
        ExportLabel.universe -> "Вселенная"
        ExportLabel.release_year -> "Год выпуска"
        ExportLabel.genres -> "Жанры"
        ExportLabel.platforms -> "Платформы"
        ExportLabel.developers -> "Разработчики"
        ExportLabel.tags -> "Теги"
        ExportLabel.completion_dates -> "Даты прохождений"
        ExportLabel.playtime -> "Время в игре"
        ExportLabel.opinion -> "Моё мнение"
        ExportLabel.hours -> "ч"
        ExportLabel.cover_link -> "Ссылка на обложку"
        ExportLabel.playing -> "Прохожу"
        ExportLabel.completed -> "Пройдено"
        ExportLabel.replaying -> "Перепрохожу"
        ExportLabel.dropped -> "Брошено"
    }
}
