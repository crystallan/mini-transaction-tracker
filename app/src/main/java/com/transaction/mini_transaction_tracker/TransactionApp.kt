package com.transaction.mini_transaction_tracker

import android.app.Application
import com.transaction.mini_transaction_tracker.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TransactionApp : Application(){
    override fun onCreate() {

        super.onCreate()
        startKoin{
            androidContext(this@TransactionApp)
            modules(AppModule.all)
        }
    }
}
