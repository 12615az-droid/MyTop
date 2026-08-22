package com.bobon.mygametop.data.di

import androidx.room.Room
import com.bobon.mygametop.data.GameRepositoryImpl
import com.bobon.mygametop.data.MyTopDatabase
import com.bobon.mygametop.domain.repository.GameRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { Room.databaseBuilder(androidContext(), MyTopDatabase::class.java, "mytop.db").build() }
    single { get<MyTopDatabase>().gameDao() }
    single<GameRepository> { GameRepositoryImpl(get()) }
}
