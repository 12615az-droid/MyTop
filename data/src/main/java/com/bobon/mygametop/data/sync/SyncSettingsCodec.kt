package com.bobon.mygametop.data.sync

import com.bobon.mygametop.domain.sync.SyncSettings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal object SyncSettingsCodec {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    fun encode(settings: SyncSettings): String = settings.normalized().let {
        json.encodeToString(SyncPreferences(it.enabled, it.serverUrl))
    }
    fun decode(value: String?): SyncSettings = runCatching {
        json.decodeFromString<SyncPreferences>(requireNotNull(value)).let { SyncSettings(it.enabled, it.serverUrl) }.normalized().let {
            if (it.enabled && !it.validServerUrl) it.copy(enabled = false) else it
        }
    }.getOrDefault(SyncSettings())
}

@Serializable
private data class SyncPreferences(val enabled: Boolean = false, val serverUrl: String = "")
