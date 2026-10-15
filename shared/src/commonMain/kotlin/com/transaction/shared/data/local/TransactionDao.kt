package com.transaction.shared.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM TransactionEntity")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM TransactionEntity WHERE id = :id")
    suspend fun getTransactionById(id: Int): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    // Room runs a list @Insert inside one database transaction: all rows are written or none are
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    // Emptiness check without loading every row
    @Query("SELECT EXISTS(SELECT 1 FROM TransactionEntity)")
    suspend fun hasTransactions(): Boolean
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
}