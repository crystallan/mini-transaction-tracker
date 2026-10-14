package com.transaction.mini_transaction_tracker

import android.app.Application
import android.util.Log
import com.transaction.shared.data.local.TransactionDatabase
import com.transaction.shared.di.initKoin
import com.transaction.shared.storage.TransactionBlobBackup
import org.koin.android.ext.android.getKoin

class TransactionApp : Application(){
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
        val database = getKoin().get<TransactionDatabase>()
        Log.d("TransactionApp", "Room database built successfully: $database")
        getKoin().get<TransactionBlobBackup>().start()    }
}