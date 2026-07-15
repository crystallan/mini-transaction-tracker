package com.transaction.mini_transaction_tracker.core.seed

import android.content.Context
import com.transaction.mini_transaction_tracker.core.domain.model.Transaction
import com.transaction.mini_transaction_tracker.core.domain.model.TransactionType
import org.json.JSONArray
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SeedDataSource(private val context: Context) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")

    fun loadSeedTransactions(): List<Transaction> {
        val json = context.assets.open("sample_transactions.json")
            .bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            Transaction(
                id = 0,
                amount = BigDecimal.valueOf(obj.getDouble("amount")),
                description = obj.getString("description"),
                date = LocalDateTime.parse(obj.getString("date"), dateFormatter),
                type = TransactionType.valueOf(obj.getString("type").uppercase())
            )
        }
    }
}