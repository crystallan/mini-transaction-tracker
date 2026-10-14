package com.transaction.shared.storage

import com.transaction.shared.domain.model.Transaction

internal fun Transaction.toBlobDto() = TransactionBlobDto(
    id = id,
    amount = amount,
    description = description,
    date = date,
    type = type
)

internal fun TransactionBlobDto.toTransaction() = Transaction(
    id = id,
    amount = amount,
    description = description,
    date = date,
    type = type
)