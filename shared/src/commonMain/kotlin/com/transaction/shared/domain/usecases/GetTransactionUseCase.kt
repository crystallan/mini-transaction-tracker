package com.transaction.shared.domain.usecases

import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.repository.TransactionRepository
import com.transaction.shared.domain.utils.OrderType
import com.transaction.shared.domain.utils.TransactionFilter
import com.transaction.shared.domain.utils.TransactionOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTransactionUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(
        transactionOrder: TransactionOrder = TransactionOrder.Date(OrderType.Descending),
        filter: TransactionFilter = TransactionFilter()
    ): Flow<List<Transaction>> {
        return repository.getAllTransactions().map { transactions ->
            val filtered = transactions.filter { it.matches(filter) }
            sort(filtered, transactionOrder)
        }
    }

    private fun Transaction.matches(filter: TransactionFilter): Boolean {
        val matchesType = filter.type == null || type == filter.type
        val matchesKeyword = filter.keyword.isBlank() ||
                description.contains(filter.keyword, ignoreCase = true)
        val matchesDateRange = filter.startDate == null || filter.endDate == null ||
                date in filter.startDate..filter.endDate
        return matchesType && matchesKeyword && matchesDateRange
    }

    private fun sort(transactions: List<Transaction>, transactionOrder: TransactionOrder): List<Transaction> {
        return when (transactionOrder.orderType) {
            is OrderType.Ascending -> {
                when (transactionOrder) {
                    is TransactionOrder.Amount -> transactions.sortedBy { it.amount }
                    is TransactionOrder.Date -> transactions.sortedBy { it.date }
                    is TransactionOrder.Description -> transactions.sortedBy { it.description }
                }
            }
            is OrderType.Descending -> {
                when (transactionOrder) {
                    is TransactionOrder.Amount -> transactions.sortedByDescending { it.amount }
                    is TransactionOrder.Date -> transactions.sortedByDescending { it.date }
                    is TransactionOrder.Description -> transactions.sortedByDescending { it.description }
                }
            }
        }
    }
}