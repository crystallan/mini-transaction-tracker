package com.transaction.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.transaction.shared.data.local.DatabaseBuilderFactory
import com.transaction.shared.domain.seed.SeedAssetReader
import com.transaction.shared.storage.AndroidKeystoreManager
import com.transaction.shared.storage.KeystoreManager
import com.transaction.shared.storage.createTransactionDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataBaseModule: Module = module {
    single {
        DatabaseBuilderFactory(androidContext())
    }
    single { SeedAssetReader() }
    single<DataStore<Preferences>> { createTransactionDataStore(androidContext()) }
    single<KeystoreManager> { AndroidKeystoreManager() }
}