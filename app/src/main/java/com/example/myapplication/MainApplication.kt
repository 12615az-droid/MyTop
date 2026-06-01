package com.example.myapplication

import android.app.Application
import com.example.myapplication.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Передаём контекст приложения, чтобы репозитории могли им пользоваться
            androidContext(this@MainApplication)
            modules(appModule)
        }

    }
}