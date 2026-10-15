package com.transaction.shared.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

/** Remembers that seed data was applied, so it appears once and never returns after the user deletes it. */
class SeedStateStore(private val dataStore: DataStore<Preferences>) {

    // Versioned key: bump it if a future release ships new seed data that should be applied
    private val seededKey = booleanPreferencesKey("seed_completed_v1")

    suspend fun isSeeded(): Boolean = dataStore.data.first()[seededKey] ?: false

    suspend fun markSeeded() {
        dataStore.edit { it[seededKey] = true }
    }
}