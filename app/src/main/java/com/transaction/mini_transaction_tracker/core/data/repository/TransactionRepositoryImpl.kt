package com.transaction.mini_transaction_tracker.core.data.repository

import com.transaction.mini_transaction_tracker.core.data.local.TransactionDao
import com.transaction.mini_transaction_tracker.core.data.local.toDomain
import com.transaction.mini_transaction_tracker.core.data.local.toEntity
import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import com.transaction.mini_transaction_tracker.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.LocalDateTime

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> =
        dao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override suspend fun getTransactionById(id: Int): Transaction? =
        dao.getTransactionById(id)?.toDomain()

    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        dao.getTransactionsByType(type).map { list -> list.map { it.toDomain() } }


    override fun getTransactionsByDate(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Transaction>> =
        dao.getTransactionsByDate(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByDescription(description: String): Flow<List<Transaction>> =
        dao.getTransactionsByDescription(description).map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByAmount(
        minAmount: BigDecimal,
        maxAmount: BigDecimal
    ): Flow<List<Transaction>> =
        dao.getTransactionsByAmount(minAmount, maxAmount).map { list -> list.map { it.toDomain() } }

    override suspend fun insertTransaction(transaction: Transaction) =
        dao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        dao.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        dao.deleteTransaction(transaction.toEntity())
}
