package com.example.nammasantheledger.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.nammasantheledger.ui.theme.PrimarySaffron

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCustomers: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = onNavigateToHome,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimarySaffron,
                selectedTextColor = PrimarySaffron,
                indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Customers") },
            label = { Text("Customers") },
            selected = currentRoute == "customer_list",
            onClick = onNavigateToCustomers,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimarySaffron,
                selectedTextColor = PrimarySaffron,
                indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            )
        )
    }
}
