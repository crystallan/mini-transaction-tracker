package com.transaction.shared.di

object AppModule {
    val all = listOf(
        DataBaseModule.module,
        platformDataBaseModule,
        BlobBackupModule.module,
        TransactionModule.module,
    )
}