package com.transaction.mini_transaction_tracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                onTransactionClick = {

                },
                onNewTransactionClick = {

                }
            )
            
        }
    }
}