package com.transaction.shared.domain.seed

import com.transaction.shared.domain.model.Transaction
import com.transaction.shared.domain.model.TransactionType
import com.transaction.shared.domain.utils.toDecimalOrNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import kotlinx.datetime.format.optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class SeedTransactionDto(
    val id: String? = null,
    // Raw JSON number: its exact source text is converted to Decimal, never through Double
    val amount: JsonPrimitive,
    val description: String,
    val date: String,
    val type: String,
    val category: String? = null
)

class SeedDataSource(private val assetReader: SeedAssetReader) {

    private val jsonConfiguration = Json {
        ignoreUnknownKeys = true
    }

    // yyyy-MM-dd HH:mm:ss with an optional .fraction (1–9 digits)
    private val seedDateFormat = LocalDateTime.Format {
        date(LocalDate.Formats.ISO)
        char(' ')
        hour(); char(':'); minute(); char(':'); second()
        optional {
            char('.')
            secondFraction(1, 9)
        }
    }

    suspend fun loadSeedTransactions(): List<Transaction> {
        val json = assetReader.readSeedJson()
        val dtos = jsonConfiguration.decodeFromString<List<SeedTransactionDto>>(json)
        return dtos.mapIndexed { index, dto -> dto.toTransaction(row = index) }
    }

    private fun SeedTransactionDto.toTransaction(row: Int): Transaction {
        // Fail fast: seed data is shipped with the app, so a bad row is a build-time bug
        fun invalid(field: String, value: String): Nothing =
            error("Invalid seed $field at row $row: '$value'")

        val amountText = amount.content
        val type = TransactionType.entries.firstOrNull { it.name.equals(type, ignoreCase = true) }
            ?: invalid("type", type)

        return Transaction(
            id = 0, // Room auto-generates the real id
            amount = amountText.toDecimalOrNull() ?: invalid("amount", amountText),
            description = description,
            date = seedDateFormat.parseOrNull(date) ?: invalid("date", date),
            type = type
        )
    }
}