package com.transaction.shared.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initKoin(context: Context) {
    startKoin {
        androidContext(context)
        modules(AppModule.all + platformDataBaseModule)
    }
    seedDatabase()
}