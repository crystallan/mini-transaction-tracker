package com.transaction.shared.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import com.transaction.shared.storage.appDataDirectoryPath

actual class DatabaseBuilderFactory {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<TransactionDatabase> =
        Room.databaseBuilder<TransactionDatabase>(
            name = "${appDataDirectoryPath()}/transaction.db"
        )
}