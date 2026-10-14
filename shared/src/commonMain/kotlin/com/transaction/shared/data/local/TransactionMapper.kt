package com.transaction.shared.data.local

import com.transaction.shared.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    amount = amount,
    description = description,
    date = date,
    type = type
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    amount = amount,
    description = description,
    date = date,
    type = type
)