package com.transaction.shared.domain.seed

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.NSUTF8StringEncoding

actual class SeedAssetReader {
    @OptIn(ExperimentalForeignApi::class)
    actual fun readSeedJson(): String {
        val path = NSBundle.mainBundle.pathForResource("sample_transactions", ofType = "json")
            ?: error("Seed file not found in bundle")
        return NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, error = null) as String
    }
}