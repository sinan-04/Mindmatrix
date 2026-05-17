package com.example.nammasantheledger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nammasantheledger.data.LedgerRepository
import com.example.nammasantheledger.ui.screens.*
import com.example.nammasantheledger.viewmodel.*

@Composable
fun NavGraph(repository: LedgerRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
            HomeScreen(
                viewModel = homeViewModel,
                onAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onAddCustomer = { navController.navigate(Screen.AddCustomer.route) },
                onNavigateToCustomers = { navController.navigate(Screen.CustomerList.route) },
                onNavigateToHome = { /* Already here */ }
            )
        }

        composable(Screen.CustomerList.route) {
            val customerViewModel: CustomerViewModel = viewModel(factory = CustomerViewModelFactory(repository))
            CustomerListScreen(
                viewModel = customerViewModel,
                onCustomerClick = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToCustomers = { /* Already here */ }
            )
        }

        composable(Screen.AddCustomer.route) {
            val customerViewModel: CustomerViewModel = viewModel(factory = CustomerViewModelFactory(repository))
            AddCustomerScreen(
                viewModel = customerViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddTransaction.route) {
            val transactionViewModel: TransactionViewModel = viewModel(factory = TransactionViewModelFactory(repository))
            val customerViewModel: CustomerViewModel = viewModel(factory = CustomerViewModelFactory(repository))
            AddTransactionScreen(
                transactionViewModel = transactionViewModel,
                customerViewModel = customerViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CustomerDetail.route,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: return@composable
            val transactionViewModel: TransactionViewModel = viewModel(factory = TransactionViewModelFactory(repository))
            CustomerDetailScreen(
                customerId = customerId,
                viewModel = transactionViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
