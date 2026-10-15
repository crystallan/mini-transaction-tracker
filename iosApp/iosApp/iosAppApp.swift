//
//  iosAppApp.swift
//  iosApp
//
//  Created by Allan Korir on 15/10/2026.
//

import SwiftUI
import sharedKit

@main
struct iosAppApp: App {
    init() {
        // Kotlin's initKoinIOS() is exposed as doInitKoinIOS(): Kotlin/Native prefixes "init…" functions with "do"
        InitKoin_iosKt.doInitKoinIOS()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
