package com.transaction.shared.storage

import androidx.datastore.preferences.core.stringPreferencesKey

object TransactionPrefsKeys {
    val TRANSACTIONS_JSON: androidx.datastore.preferences.core.Preferences.Key<String> =
        stringPreferencesKey("transactions_json")
}