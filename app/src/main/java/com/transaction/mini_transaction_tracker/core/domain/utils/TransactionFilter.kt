package com.transaction.mini_transaction_tracker.core.domain.utils

import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionFilter (
    val type: TransactionType? = null,
    val keyword: String = "",
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)