package com.transaction.shared.domain.utils

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}