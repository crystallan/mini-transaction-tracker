package com.transaction.shared.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun createTransactionDataStore(): DataStore<Preferences> = createDataStore {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    // Safely unwrap the nullable path to avoid platform nullability warnings
    val documentPath = requireNotNull(documentDirectory?.path) { "Documents directory not found" }
    "$documentPath/$TRANSACTIONS_DATASTORE_FILE"
}