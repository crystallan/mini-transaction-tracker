package com.transaction.shared.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createTransactionDataStore(): DataStore<Preferences> = createDataStore {
    "${appDataDirectoryPath()}/$TRANSACTIONS_DATASTORE_FILE"
}