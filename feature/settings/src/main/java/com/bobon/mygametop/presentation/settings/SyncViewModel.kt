package com.bobon.mygametop.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobon.mygametop.domain.sync.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SyncUiState(
    val saved: SyncSettings = SyncSettings(),
    val serverUrl: String = "",
    val invalidUrl: Boolean = false,
    val busy: Boolean = false,
    val result: SyncResult? = null
) {
    val canCheck: Boolean get() = saved.enabled && !busy && serverUrl.trim().trimEnd('/') == saved.serverUrl
}

class SyncViewModel(private val repository: SyncSettingsRepository, private val client: SyncClient) : ViewModel() {
    private val mutableState = MutableStateFlow(SyncUiState(repository.settings.value, repository.settings.value.serverUrl))
    val state = mutableState.asStateFlow()

    fun changeServerUrl(value: String) {
        mutableState.value = state.value.copy(serverUrl = value, invalidUrl = false, result = null)
    }

    fun setEnabled(enabled: Boolean) = save(
        state.value.saved.copy(enabled = enabled, serverUrl = state.value.serverUrl), validateAddress = enabled
    )
    fun saveSettings() = save(state.value.saved.copy(serverUrl = state.value.serverUrl))

    private fun save(settings: SyncSettings, validateAddress: Boolean = true) {
        if (state.value.busy) return
        val next = settings.normalized()
        if ((next.enabled || validateAddress && next.serverUrl.isNotEmpty()) && !next.validServerUrl) {
            mutableState.value = state.value.copy(invalidUrl = true)
            return
        }
        repository.save(next)
        mutableState.value = state.value.copy(saved = next, serverUrl = next.serverUrl, invalidUrl = false, result = null)
    }

    fun checkSynchronization() {
        if (!state.value.canCheck) return
        mutableState.value = state.value.copy(busy = true, result = null)
        viewModelScope.launch {
            try {
                mutableState.value = state.value.copy(result = client.synchronize(repository.settings.value))
            } catch (error: CancellationException) { throw error }
            catch (_: Exception) {
                mutableState.value = state.value.copy(result = SyncResult.SERVER_NOT_AVAILABLE)
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }
}
