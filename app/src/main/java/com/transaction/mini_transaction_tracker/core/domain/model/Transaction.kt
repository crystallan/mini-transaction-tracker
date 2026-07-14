package com.transaction.mini_transaction_tracker.core.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Transaction (
    val id: Int = 0,
    val amount : BigDecimal,
    val description : String,
    val date : LocalDateTime,
    val type : TransactionType
)