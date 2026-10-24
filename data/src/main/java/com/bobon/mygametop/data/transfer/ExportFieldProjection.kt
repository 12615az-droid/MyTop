package com.bobon.mygametop.data.transfer

import com.bobon.mygametop.domain.transfer.GameExportOptions

/** JSON schema names belong to the file adapter, not to the export selection policy. */
val GameExportOptions.omittedFields: Set<String>
    get() = buildSet {
        if (!includeMetadata) addAll(listOf("series", "universe", "genres", "tags", "developers", "platforms", "releaseYear"))
        if (!includeHistory) addAll(listOf("completionDates", "playtimeHours"))
        if (!includeOpinion) add("opinion")
        if (!includeCover) add("coverUri")
    }
