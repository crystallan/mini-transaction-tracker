package com.transaction.shared.domain.seed

import android.content.Context

actual class SeedAssetReader(private val context: Context) {
    actual fun readSeedJson(): String =
        context.assets.open("sample_transactions.json")
            .bufferedReader()
            .use { it.readText() }
}