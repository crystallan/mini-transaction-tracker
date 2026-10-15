package com.transaction.shared.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.transaction.shared.presentation.transaction.add.presentation.AddTransactionScreen
import com.transaction.shared.presentation.transaction.detail.presentation.TransactionDetailScreen
import com.transaction.shared.presentation.transaction.edit.presentation.EditTransactionScreen
import com.transaction.shared.presentation.transaction.view.presentation.ViewTransactionScreen

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
            val transactionId = backStackEntry.arguments?.read { getInt("transactionId") } ?: return@composable
            TransactionDetailScreen(
                transactionId = transactionId,
                onNavigateBack = { navController.popBackStack() },
                onEditClick = {navController.navigate(AppDestinations.edit(transactionId))}
            )
        }
        composable(AppDestinations.ADD) {
            AddTransactionScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = AppDestinations.EDIT,
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.read { getInt("transactionId") } ?: return@composable
            EditTransactionScreen(
                transactionId = transactionId,
                onNavigateBack = {
                    navController.popBackStack() }
            )
        }
    }
}