package com.transaction.mini_transaction_tracker.core.domain.utils

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}