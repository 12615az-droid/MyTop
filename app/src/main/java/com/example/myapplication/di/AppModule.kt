package com.bobon.mypace.di

import androidx.room.Room
import com.bobon.mypace.database.AppDatabase
import com.example.myapplication.MainRepository
import com.example.myapplication.ui.ViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {

    single {

        MainRepository(
            context = get(),
            workoutDao = get()
        )
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "pace_db"
        ).build()
    }

    single {
        get<AppDatabase>().workoutDao()
    }
    single { WorkoutData }


    viewModel { ViewModel(repository = get()) }
}
