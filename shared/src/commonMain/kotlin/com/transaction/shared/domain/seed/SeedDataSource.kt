package com.transaction.shared.domain.seed

import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.model.TransactionType
import com.transaction.shared.domain.utils.toDecimalOrNull
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SeedTransactionDto(
    val id: String? = null,
    val amount: Double,
    val description: String,
    val date: String,
    val type: String,
    val category: String? = null
)

class SeedDataSource(private val assetReader: SeedAssetReader) {

    private val jsonConfiguration = Json {
        ignoreUnknownKeys = true
    }

    fun loadSeedTransactions(): List<Transaction> {
        val json = assetReader.readSeedJson()
        val dtos = jsonConfiguration.decodeFromString<List<SeedTransactionDto>>(json)
        return dtos.map { dto ->
            Transaction(
                id = 0,
                amount = dto.amount.toString().toDecimalOrNull()
                    ?: error("Invalid seed amount: ${dto.amount}"),
                description = dto.description,
                date = parseSeedDate(dto.date),
                type = TransactionType.valueOf(dto.type.uppercase())
            )
        }
    }

    private fun parseSeedDate(raw: String): LocalDateTime {
        val (datePart, timePart) = raw.split(" ")
        val (year, month, day) = datePart.split("-").map { it.toInt() }
        val mainAndFraction = timePart.split(".")
        val (hour, minute, second) = mainAndFraction[0].split(":").map { it.toInt() }
        val nanosecond = if (mainAndFraction.size > 1)
            mainAndFraction[1].padEnd(9, '0').take(9).toInt()
        else 0
        return LocalDateTime(year, month, day, hour, minute, second, nanosecond)
    }
}