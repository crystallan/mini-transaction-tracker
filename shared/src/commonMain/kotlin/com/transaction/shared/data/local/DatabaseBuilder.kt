package com.transaction.shared.data.local

import androidx.room.RoomDatabase

expect class DatabaseBuilderFactory {
    fun getDatabaseBuilder(): RoomDatabase.Builder<TransactionDatabase>
}