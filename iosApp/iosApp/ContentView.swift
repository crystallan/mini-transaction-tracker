//
//  ContentView.swift
//  iosApp
//
//  Created by Allan Korir on 15/10/2026.
//

import SwiftUI
import UIKit
import sharedKit

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        // Compose handles insets itself, so let it draw edge to edge
        ComposeView()
            .ignoresSafeArea()
    }
}
