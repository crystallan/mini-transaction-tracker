package com.transaction.shared.domain.seed

expect class SeedAssetReader {
    fun readSeedJson(): String
}