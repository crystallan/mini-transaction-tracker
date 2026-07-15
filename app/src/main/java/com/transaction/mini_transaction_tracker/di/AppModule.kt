package com.transaction.mini_transaction_tracker.di

object AppModule {
    val all = listOf(
        DataBaseModule.module,
        TransactionModule.module,
    )
}