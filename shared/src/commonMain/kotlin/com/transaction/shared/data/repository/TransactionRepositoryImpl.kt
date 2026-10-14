package com.transaction.shared.data.repository

import com.transaction.shared.data.local.TransactionDao
import com.transaction.shared.data.local.toDomain
import com.transaction.shared.data.local.toEntity
import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> =
        dao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override suspend fun getTransactionById(id: Int): Transaction? =
        dao.getTransactionById(id)?.toDomain()

    override suspend fun insertTransaction(transaction: Transaction) =
        dao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        dao.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        dao.deleteTransaction(transaction.toEntity())
}