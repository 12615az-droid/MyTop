package com.bobon.mygametop.data

import androidx.room3.Room
import com.bobon.mygametop.data.MyTopDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<MyTopDatabase> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = MyTopDatabase::class.java,
            name = "mytop.db"
        ).build()
    }

    single {
        get<MyTopDatabase>().gameDao()
    }
}