package com.transaction.shared.di

import com.transaction.shared.storage.EncryptedLocalStorageRepo
import com.transaction.shared.storage.TransactionBlobBackup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

object BlobBackupModule {
    val module = module {
        single { EncryptedLocalStorageRepo(get(), get()) }
        single {
            TransactionBlobBackup(
                repository = get(),
                blobRepo = get(),
                scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            )
        }
    }
}