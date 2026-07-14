package com.transaction.mini_transaction_tracker.core.domain.repository

import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDateTime

interface TransactionRepository {

    fun getAllTransactions(): Flow<List<Transaction>>

    suspend fun getTransactionById(id: Int): Transaction?

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    fun getTransactionsByDate(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>>

    fun getTransactionsByDescription(description: String): Flow<List<Transaction>>

    fun getTransactionsByAmount(minAmount: BigDecimal, maxAmount: BigDecimal): Flow<List<Transaction>>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

}