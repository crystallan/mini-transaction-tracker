package com.transaction.shared.domain.utils

import io.github.kormium.decimal.Decimal

fun String.toDecimalOrNull(): Decimal? =
    runCatching { Decimal.parse(this) }.getOrNull()
