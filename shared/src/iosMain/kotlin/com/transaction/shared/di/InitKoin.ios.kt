package com.transaction.shared.di

// Called once from the Swift app's init, before MainViewController() is shown.
fun initKoinIOS() {
    initKoin(platformDataBaseModule)
}
