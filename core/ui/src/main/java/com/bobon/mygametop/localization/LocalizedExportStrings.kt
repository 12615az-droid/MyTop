package com.bobon.mygametop.localization

import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.domain.transfer.ExportLabel
import com.bobon.mygametop.domain.transfer.TextExportStrings

class LocalizedExportStrings(private val strings: AppStrings) : TextExportStrings {
    override val locale get() = strings.locale
    override fun get(label: ExportLabel): String = strings.get(when (label) {
        ExportLabel.app_name -> R.string.app_name
        ExportLabel.total_games -> R.string.total_games
        ExportLabel.average_rating -> R.string.average_rating
        ExportLabel.empty_collection -> R.string.empty_collection
        ExportLabel.status -> R.string.status
        ExportLabel.completions -> R.string.completions
        ExportLabel.series -> R.string.series
        ExportLabel.universe -> R.string.universe
        ExportLabel.release_year -> R.string.release_year
        ExportLabel.genres -> R.string.genres
        ExportLabel.platforms -> R.string.platforms
        ExportLabel.developers -> R.string.developers
        ExportLabel.tags -> R.string.tags
        ExportLabel.completion_dates -> R.string.completion_dates
        ExportLabel.playtime -> R.string.playtime
        ExportLabel.opinion -> R.string.opinion
        ExportLabel.hours -> R.string.hours
        ExportLabel.cover_link -> R.string.cover_link
        ExportLabel.playing -> R.string.playing
        ExportLabel.completed -> R.string.completed
        ExportLabel.replaying -> R.string.replaying
        ExportLabel.dropped -> R.string.dropped
    })
}
