package com.bobon.mygametop.data.sync

import com.bobon.mygametop.domain.sync.*

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncSettingsStore(context: Context) : SyncSettingsRepository {
    private val preferences = context.getSharedPreferences("sync", Context.MODE_PRIVATE)
    private val mutableSettings = MutableStateFlow(SyncSettingsCodec.decode(preferences.getString("settings", null)))
    override val settings = mutableSettings.asStateFlow()

    @Synchronized
    override fun save(settings: SyncSettings) {
        val next = settings.normalized()
        require(!next.enabled || next.validServerUrl)
        preferences.edit { putString("settings", SyncSettingsCodec.encode(next)) }
        mutableSettings.value = next
    }
}
