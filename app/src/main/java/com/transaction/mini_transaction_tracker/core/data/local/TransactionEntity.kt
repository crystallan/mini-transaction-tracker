package com.transaction.mini_transaction_tracker.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
data class TransactionEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount : BigDecimal,
    val description : String,
    val date : LocalDateTime,
    val type : TransactionType
    )
