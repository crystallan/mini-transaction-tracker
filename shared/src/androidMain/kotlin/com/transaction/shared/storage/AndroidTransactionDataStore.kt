package com.transaction.shared.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createTransactionDataStore(context: Context): DataStore<Preferences> =
    createDataStore {
        context.filesDir.resolve("datastore/$TRANSACTIONS_DATASTORE_FILE").absolutePath
    }