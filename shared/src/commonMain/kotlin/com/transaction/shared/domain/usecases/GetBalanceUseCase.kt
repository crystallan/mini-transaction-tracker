package com.transaction.shared.domain.usecases

import com.transaction.shared.domain.model.TransactionType
import com.transaction.shared.domain.repository.TransactionRepository
import io.github.kormium.decimal.Decimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class GetBalanceUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<Decimal> {
        return repository.getAllTransactions().map { transactions ->
            transactions.fold(Decimal.ZERO) { runningBalance, transaction ->
                when (transaction.type) {
                    TransactionType.CREDIT -> runningBalance + transaction.amount
                    TransactionType.DEBIT -> runningBalance - transaction.amount
                }
            }
        }
    }
}