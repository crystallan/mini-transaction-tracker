package com.transaction.shared.di

fun initKoinIOS() {
    org.koin.core.context.startKoin {
        modules(AppModule.all + platformDataBaseModule)
    }
    seedDatabase()
}