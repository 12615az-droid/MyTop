package com.bobon.mygametop.di

import com.bobon.mygametop.domain.usecase.AddGameUseCase
import com.bobon.mygametop.domain.usecase.ObserveGamesUseCase
import com.bobon.mygametop.presentation.add.AddGameViewModel
import com.bobon.mygametop.presentation.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { AddGameUseCase(get()) }
    factory { ObserveGamesUseCase(get()) }
    viewModel { AddGameViewModel(get()) }
    viewModel { MainViewModel(get()) }
}
