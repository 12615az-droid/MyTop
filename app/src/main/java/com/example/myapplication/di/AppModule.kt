package com.example.myapplication.di

import com.example.myapplication.MainRepository
import com.example.myapplication.database.MediaItemDatabase
import com.example.myapplication.ui.ViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        MediaItemDatabase.getDatabase(androidContext())
    }

    single {

    }

    single {
        MainRepository(
            mediaItemDao = get()
        )
    }

    viewModel { ViewModel(repository = get()) }
}
