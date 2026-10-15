package com.transaction.shared.domain.seed

import com.transaction.shared.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

class SeedAssetReader {
    @OptIn(ExperimentalResourceApi::class)
    suspend fun readSeedJson(): String =
        Res.readBytes("files/sample_transactions.json").decodeToString()
}