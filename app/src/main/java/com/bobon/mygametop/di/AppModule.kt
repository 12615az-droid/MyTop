package com.bobon.mygametop.di

import com.bobon.mygametop.domain.usecase.UpdateGameUseCase
import com.bobon.mygametop.domain.usecase.AddGameUseCase
import com.bobon.mygametop.domain.usecase.ObserveGamesUseCase
import com.bobon.mygametop.domain.usecase.GetGameUseCase
import com.bobon.mygametop.domain.usecase.DeleteGameUseCase
import com.bobon.mygametop.presentation.add.AddGameViewModel
import com.bobon.mygametop.presentation.main.MainViewModel
import com.bobon.mygametop.presentation.settings.SettingsViewModel
import com.bobon.mygametop.presentation.settings.SyncViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import com.bobon.mygametop.localization.AppStrings
import com.bobon.mygametop.localization.LocalizedExportStrings
import com.bobon.mygametop.domain.transfer.TextExportStrings
import com.bobon.mygametop.domain.sync.*

val appModule = module {
    single { SyncClient(get()) }
    single { AppStrings(androidContext(), get()) }
    single<TextExportStrings> { LocalizedExportStrings(get()) }
    factory { AddGameUseCase(get()) }
    factory { UpdateGameUseCase(get()) }
    factory { ObserveGamesUseCase(get()) }
    factory { GetGameUseCase(get()) }
    factory { DeleteGameUseCase(get()) }
    viewModel { AddGameViewModel(get(), get(), get(), get()) }
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { SyncViewModel(get(), get()) }
}
