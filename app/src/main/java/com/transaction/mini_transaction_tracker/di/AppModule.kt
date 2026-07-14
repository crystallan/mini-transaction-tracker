package com.transaction.mini_transaction_tracker.di

object AppModule {
    val all = listOf(
        DataStoreModule.module,
        TransactionModule.module,
    )
}