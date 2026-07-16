package com.transaction.mini_transaction_tracker.core.data.local

import androidx.room.TypeConverter
import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? = value?.let { BigDecimal(it) }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?) = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? = value?.let { TransactionType.valueOf(it) }

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name
}