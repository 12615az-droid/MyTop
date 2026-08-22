package com.bobon.mygametop.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobon.mygametop.domain.model.Game
import com.bobon.mygametop.domain.usecase.ObserveGamesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(observeGames: ObserveGamesUseCase) : ViewModel() {
    val games: StateFlow<List<Game>> = observeGames().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}
