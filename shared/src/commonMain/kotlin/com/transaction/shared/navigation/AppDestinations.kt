package com.transaction.shared.navigation

object AppDestinations {
    const val HOME = "home"
    const val ADD = "add"

    private const val DETAIL_ROUTE = "detail"
    const val DETAIL = "$DETAIL_ROUTE/{transactionId}"

    const val EDIT = "edit/{transactionId}"
    fun edit(id: Int) = "edit/$id"

    fun detail(transactionId: Int): String = "$DETAIL_ROUTE/$transactionId"
}