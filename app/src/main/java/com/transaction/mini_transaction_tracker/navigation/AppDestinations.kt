package com.transaction.mini_transaction_tracker.navigation

object AppDestinations {
    const val HOME = "home"
    const val ADD = "add"

    private const val DETAIL_ROUTE = "detail"
    const val DETAIL = "$DETAIL_ROUTE/{transactionId}"

    fun detail(transactionId: Int): String = "$DETAIL_ROUTE/$transactionId"
}