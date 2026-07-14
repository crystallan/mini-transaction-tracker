package com.transaction.mini_transaction_tracker.core.data.local

import com.transaction.mini_transaction_tracker.core.domain.model.Transaction

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