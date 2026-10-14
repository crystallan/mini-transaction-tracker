package com.transaction.shared.domain.utils

import com.transaction.shared.domain.model.TransactionType
import kotlinx.datetime.LocalDateTime

data class TransactionFilter (
    val type: TransactionType? = null,
    val keyword: String = "",
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)