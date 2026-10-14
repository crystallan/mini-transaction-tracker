package com.transaction.shared.domain.utils

sealed class TransactionOrder(val orderType: OrderType) {
    class Amount( orderType: OrderType) : TransactionOrder(orderType)
    class Date( orderType: OrderType) : TransactionOrder(orderType)
    class Description( orderType: OrderType) : TransactionOrder(orderType)
}