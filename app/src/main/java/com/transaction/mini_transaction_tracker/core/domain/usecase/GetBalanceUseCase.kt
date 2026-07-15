package com.transaction.mini_transaction_tracker.core.domain.usecase

import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import com.transaction.mini_transaction_tracker.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

class GetBalanceUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<BigDecimal> {
        return repository.getAllTransactions().map { transactions ->
            transactions.fold(BigDecimal.ZERO) { runningBalance, transaction ->
                when (transaction.type) {
                    TransactionType.CREDIT -> runningBalance + transaction.amount
                    TransactionType.DEBIT -> runningBalance - transaction.amount
                }
            }
        }
    }
}