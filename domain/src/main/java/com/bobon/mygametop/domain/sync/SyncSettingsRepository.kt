package com.bobon.mygametop.domain.sync

import kotlinx.coroutines.flow.StateFlow

interface SyncSettingsRepository {
    val settings: StateFlow<SyncSettings>
    fun save(settings: SyncSettings)
}
