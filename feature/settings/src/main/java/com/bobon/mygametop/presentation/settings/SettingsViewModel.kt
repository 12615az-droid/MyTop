package com.bobon.mygametop.presentation.settings

import androidx.lifecycle.ViewModel
import com.bobon.mygametop.domain.settings.AppearanceRepository
import com.bobon.mygametop.domain.settings.AppearanceSettings

class SettingsViewModel(private val appearance: AppearanceRepository) : ViewModel() {
    val settings = appearance.settings
    fun update(transform: (AppearanceSettings) -> AppearanceSettings) = appearance.update(transform)
    fun reset() = appearance.reset()
    suspend fun importFont(documentUri: String) = appearance.importFont(documentUri)
}
