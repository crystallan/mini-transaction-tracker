package com.transaction.mini_transaction_tracker.core.data.local

import androidx.room.Database
import androidx.room.TypeConverters

@Database(
    entities = [TransactionEntity::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class TransactionDatabase {
    abstract fun transactionDao(): TransactionDao
}