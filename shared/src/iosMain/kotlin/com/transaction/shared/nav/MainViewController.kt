package com.transaction.shared.nav

import com.transaction.shared.di.initKoin
import com.transaction.shared.di.platformDataBaseModule

fun initSharedKoin() {
    initKoin(platformDataBaseModule)
}