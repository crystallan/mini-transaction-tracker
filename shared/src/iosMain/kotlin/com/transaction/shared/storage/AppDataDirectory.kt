package com.transaction.shared.storage

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSNumber
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDomainMask

private const val APP_DATA_DIRECTORY = "AppData"

/**
 * Private directory for the Room DB and DataStore.
 *
 * Application Support: app data that isn't user-visible (Documents is for user files).
 * Excluded from backup: the encryption key uses kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly,
 * so it never leaves this device. Restored data on a new device could not be decrypted.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal fun appDataDirectoryPath(): String = memScoped {
    val error = alloc<ObjCObjectVar<NSError?>>()
    val fileManager = NSFileManager.defaultManager

    val appSupport = fileManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true, // Application Support is not created automatically, unlike Documents
        error = error.ptr
    ) ?: error("Unable to resolve Application Support: ${error.value?.localizedDescription}")

    val dataDir = appSupport.URLByAppendingPathComponent(APP_DATA_DIRECTORY, isDirectory = true)
        ?: error("Unable to build app data directory URL")

    // Succeeds without error if the directory already exists
    val created = fileManager.createDirectoryAtURL(
        url = dataDir,
        withIntermediateDirectories = true,
        attributes = null,
        error = error.ptr
    )
    check(created) { "Unable to create app data directory: ${error.value?.localizedDescription}" }

    // Excluding the directory also excludes Room's -wal and -shm side files
    val excluded = dataDir.setResourceValue(
        value = NSNumber(bool = true),
        forKey = NSURLIsExcludedFromBackupKey,
        error = error.ptr
    )
    check(excluded) { "Unable to exclude app data from backup: ${error.value?.localizedDescription}" }

    dataDir.path ?: error("App data directory has no file path")
}