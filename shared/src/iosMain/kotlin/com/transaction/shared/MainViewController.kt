package com.transaction.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.transaction.shared.navigation.NavGraph
import com.transaction.shared.ui.theme.Mini_Transaction_TrackerTheme
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    Mini_Transaction_TrackerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph()
        }
    }
}
