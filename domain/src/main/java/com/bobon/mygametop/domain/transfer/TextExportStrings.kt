package com.bobon.mygametop.domain.transfer

import java.util.Locale

/** Semantic labels keep exported reports independent of Android resource identifiers. */
enum class ExportLabel {
    app_name, total_games, average_rating, empty_collection, status, completions, series, universe,
    release_year, genres, platforms, developers, tags, completion_dates, playtime, opinion, hours,
    cover_link, playing, completed, replaying, dropped
}

interface TextExportStrings {
    val locale: Locale
    fun get(label: ExportLabel): String
}
