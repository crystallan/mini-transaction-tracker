package com.transaction.shared.data.local

import androidx.room.TypeConverter
import com.transaction.shared.domain.model.TransactionType
import io.github.kormium.decimal.Decimal
import kotlinx.datetime.LocalDateTime

class Converters {

    @TypeConverter
    fun fromBigDecimal(value: Decimal?): String? = value?.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String?): Decimal? = value?.let { Decimal(it) }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?) = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? = value?.let { TransactionType.valueOf(it) }

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name
}