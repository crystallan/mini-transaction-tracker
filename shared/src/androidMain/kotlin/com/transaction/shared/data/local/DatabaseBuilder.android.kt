package com.transaction.shared.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseBuilderFactory(private val context: Context) {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<TransactionDatabase> {
        val dbFile = context.getDatabasePath("transaction.db")
        return Room.databaseBuilder(
            context = context.applicationContext,
            name = dbFile.absolutePath
        )
    }
}