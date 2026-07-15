package com.transaction.mini_transaction_tracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.transaction.mini_transaction_tracker.feature.transaction.add.presentation.AddTransactionScreen
import com.transaction.mini_transaction_tracker.feature.transaction.detail.presentation.TransactionDetailScreen
import com.transaction.mini_transaction_tracker.feature.transaction.view.presentation.ViewTransactionScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable(AppDestinations.HOME){
            ViewTransactionScreen(
                onTransactionClick = { transactionId ->
                    navController.navigate(AppDestinations.detail(transactionId))
                },
                onNewTransactionClick = {
                    navController.navigate(AppDestinations.ADD)
                }
            )
        }
        composable(
            route = AppDestinations.DETAIL,
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: return@composable
            TransactionDetailScreen(
                transactionId = transactionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(AppDestinations.ADD) {
            AddTransactionScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}