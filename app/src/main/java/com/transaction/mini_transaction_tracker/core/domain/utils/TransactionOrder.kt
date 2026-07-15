package com.transaction.mini_transaction_tracker.core.domain.utils

sealed class TransactionOrder(val orderType: OrderType) {
    class Amount( orderType: OrderType) : TransactionOrder(orderType)
    class Date( orderType: OrderType) : TransactionOrder(orderType)
    class Description( orderType: OrderType) : TransactionOrder(orderType)
    class Type( orderType: OrderType) : TransactionOrder(orderType)

}