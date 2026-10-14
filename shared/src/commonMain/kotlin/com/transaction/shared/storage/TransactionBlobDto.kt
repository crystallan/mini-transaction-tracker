package com.transaction.shared.storage

import com.transaction.shared.domain.model.TransactionType
import io.github.kormium.decimal.Decimal
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TransactionBlobDto(
    val id: Int = 0,
    @Serializable(with = DecimalAsStringSerializer::class)
    val amount: Decimal,
    val description: String,
    val date: LocalDateTime, // kotlinx-datetime ships an ISO-8601 serializer
    val type: TransactionType
)