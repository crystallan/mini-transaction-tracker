package com.transaction.mini_transaction_tracker.core.domain.repository

import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getAllTransactions(): Flow<List<Transaction>>

    suspend fun getTransactionById(id: Int): Transaction?

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

}