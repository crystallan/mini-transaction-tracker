package com.transaction.mini_transaction_tracker

import android.app.Application
import com.transaction.mini_transaction_tracker.core.domain.usecase.SeedDatabaseUseCase
import com.transaction.mini_transaction_tracker.di.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TransactionApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@TransactionApp)
            modules(AppModule.all)
        }
        val seedDatabaseUseCase : SeedDatabaseUseCase by inject()
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseUseCase()
        }
    }
}
