package com.transaction.shared.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.transaction.shared.domain.model.TransactionType
import io.github.kormium.decimal.Decimal
import kotlinx.datetime.LocalDateTime

@Entity
data class TransactionEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount : Decimal,
    val description : String,
    val date : LocalDateTime,
    val type : TransactionType
    )
