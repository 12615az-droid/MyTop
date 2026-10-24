package com.bobon.mygametop.domain.settings

import kotlinx.coroutines.flow.StateFlow

interface AppearanceRepository {
    val settings: StateFlow<AppearanceSettings>
    fun update(transform: (AppearanceSettings) -> AppearanceSettings)
    fun reset()
    suspend fun importFont(documentUri: String)
}
