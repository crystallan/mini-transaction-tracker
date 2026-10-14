package com.transaction.shared.domain.model

import io.github.kormium.decimal.Decimal
import kotlinx.datetime.LocalDateTime

data class Transaction (
    val id: Int = 0,
    val amount : Decimal,
    val description : String,
    val date : LocalDateTime,
    val type : TransactionType
)