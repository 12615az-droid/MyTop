package com.bobon.mygametop.presentation.main

import com.bobon.mygametop.core.ui.R
import com.bobon.mygametop.localization.AppStrings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobon.mygametop.domain.model.GameQuery
import com.bobon.mygametop.domain.usecase.DeleteGameUseCase
import com.bobon.mygametop.domain.transfer.GameExportOptions
import com.bobon.mygametop.domain.transfer.GameExportRequest
import com.bobon.mygametop.domain.usecase.ObserveGamesUseCase
import com.bobon.mygametop.domain.transfer.GameTransferRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MainViewModel(observeGames: ObserveGamesUseCase, private val transfer: GameTransferRepository, private val deleteGameUseCase: DeleteGameUseCase, private val strings: AppStrings) : ViewModel() {
    private val query = MutableStateFlow(GameQuery())
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()
    private val messagesChannel = Channel<String>(Channel.BUFFERED)
    val messages = messagesChannel.receiveAsFlow()
    private val clipboardChannel = Channel<String>(Channel.BUFFERED)
    val clipboard = clipboardChannel.receiveAsFlow()
    val state = combine(observeGames(), query, ::createMainUiState).catch {
        messagesChannel.send(strings.get(R.string.load_collection_error))
        emit(MainUiState(loading = false))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    fun updateQuery(value: GameQuery) { query.value = value }
    fun import(documentUri: String) = runTransfer(strings.get(R.string.import_error)) { strings.get(R.string.added_count, transfer.import(documentUri)) }
    private var pendingExport: GameExportRequest? = null
    fun prepareExport(options: GameExportOptions) {
        pendingExport = GameExportRequest(options, query.value)
    }
    fun finishExport(documentUri: String?) {
        val request = pendingExport
        pendingExport = null
        if (documentUri == null) return
        runTransfer(strings.get(R.string.export_error)) {
            checkNotNull(request) { strings.get(R.string.export_retry) }
            strings.get(R.string.exported_count, transfer.export(documentUri, request))
        }
    }
    fun copyExport(options: GameExportOptions) = runTransfer(strings.get(R.string.export_error)) {
        val (text, count) = transfer.render(GameExportRequest(options, query.value))
        clipboardChannel.send(text)
        strings.get(R.string.copied_count, count)
    }
    fun deleteGame(id: Long) = runTransfer(strings.get(R.string.delete_error)) {
        if (deleteGameUseCase(id)) strings.get(R.string.game_deleted) else strings.get(R.string.already_deleted)
    }
    private fun runTransfer(failureMessage: String, action: suspend () -> String) {
        if (_busy.value) return
        _busy.value = true
        viewModelScope.launch {
            try { messagesChannel.send(action()) }
            catch (error: CancellationException) { throw error }
            catch (error: Exception) {
                messagesChannel.send(failureMessage)
            } finally { _busy.value = false }
        }
    }
}
