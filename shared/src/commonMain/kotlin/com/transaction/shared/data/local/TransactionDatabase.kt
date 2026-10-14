package com.transaction.shared.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

@Database(
    entities = [TransactionEntity::class],
    version = 1
)
@ConstructedBy(TransactionDatabaseConstructor::class)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
@Suppress("KotlinNoActualForExpect")
expect object TransactionDatabaseConstructor : RoomDatabaseConstructor<TransactionDatabase> {
    override fun initialize(): TransactionDatabase
}