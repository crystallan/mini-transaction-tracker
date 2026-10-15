package com.transaction.shared.domain.repository

import com.transaction.shared.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getAllTransactions(): Flow<List<Transaction>>

    suspend fun getTransactionById(id: Int): Transaction?

    suspend fun insertTransaction(transaction: Transaction)

    /** Inserts all transactions atomically. */
    suspend fun insertTransactions(transactions: List<Transaction>)

    suspend fun hasTransactions(): Boolean

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

}