package com.transaction.mini_transaction_tracker.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDateTime

@Dao
interface TransactionDao {

    @Query("SELECT * FROM TransactionEntity")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM TransactionEntity WHERE id = :id")
    suspend fun getTransactionById(id: Int): TransactionEntity?

    @Query("SELECT * FROM TransactionEntity WHERE type = :type")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM TransactionEntity WHERE date BETWEEN :startDate AND :endDate")
    fun getTransactionsByDate(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM TransactionEntity WHERE description LIKE '%' || :description || '%'")
    fun getTransactionsByDescription(description: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM TransactionEntity WHERE amount BETWEEN :minAmount AND :maxAmount")
    fun getTransactionsByAmount(minAmount: BigDecimal, maxAmount: BigDecimal): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
}
