package com.bobon.mygametop.data.di

import androidx.room.Room
import com.bobon.mygametop.data.repository.GameRepositoryImpl
import com.bobon.mygametop.data.local.room.MyTopDatabase
import com.bobon.mygametop.data.settings.AppearanceStore
import com.bobon.mygametop.data.sync.SyncSettingsStore
import com.bobon.mygametop.data.sync.UnconfiguredSyncGateway
import com.bobon.mygametop.data.transfer.GameFileTransfer
import com.bobon.mygametop.domain.settings.AppearanceRepository
import com.bobon.mygametop.domain.sync.SyncSettingsRepository
import com.bobon.mygametop.domain.sync.SyncGateway
import com.bobon.mygametop.domain.transfer.GameTransferRepository
import com.bobon.mygametop.domain.repository.GameRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<AppearanceRepository> { AppearanceStore(androidContext()) }
    single<SyncSettingsRepository> { SyncSettingsStore(androidContext()) }
    single<SyncGateway> { UnconfiguredSyncGateway() }
    single<GameTransferRepository> { GameFileTransfer(androidContext().contentResolver, get(), get()) }
    single { Room.databaseBuilder(androidContext(), MyTopDatabase::class.java, "mytop.db").build() }
    single { get<MyTopDatabase>().gameDao() }
    single<GameRepository> { GameRepositoryImpl(get()) }
}
