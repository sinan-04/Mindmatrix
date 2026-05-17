package com.example.nammasantheledger.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object CustomerList : Screen("customer_list")
    object AddCustomer : Screen("add_customer")
    object AddTransaction : Screen("add_transaction")
    object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: Int) = "customer_detail/$customerId"
    }
}
