package com.transaction.shared.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual class DatabaseBuilderFactory {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<TransactionDatabase> {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        val basePath = documentDirectory?.path
            ?: error("Unable to resolve documents directory for Room database")
        val dbFilePath = "$basePath/transaction.db"

        return Room.databaseBuilder<TransactionDatabase>(
            name = dbFilePath
        )
    }
}