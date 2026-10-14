package com.transaction.shared.di

import com.transaction.shared.domain.usecases.SeedDatabaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.mp.KoinPlatform

fun initKoin(platformModule: Module) {
    startKoin {
        modules(AppModule.all + platformModule)
    }
    seedDatabase()
}

internal fun seedDatabase() {
    val seedDatabaseUseCase = KoinPlatform.getKoin().get<SeedDatabaseUseCase>()
    CoroutineScope(Dispatchers.IO).launch {
        seedDatabaseUseCase()
    }
}