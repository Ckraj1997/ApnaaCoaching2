package com.chandan.apnaacoaching.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chandan.apnaacoaching.navigation.Screen

@Composable
fun DashboardBottomNav(
    navController: NavController
) {
    val items = listOf(
        Screen.Practice to "Practice",
        Screen.Cart to "My Cart",
        Screen.Home to "Home",
        Screen.Content to "My Content",
        Screen.Account to "Account"
    )
    val selectedIcons = listOf(
        Icons.Filled.PendingActions,
        Icons.Filled.ShoppingCart,
        Icons.Filled.Home,
        Icons.AutoMirrored.Filled.List,
        Icons.Filled.Person
    )
    val unselectedIcons = listOf(
        Icons.Outlined.PendingActions,
        Icons.Outlined.ShoppingCart,
        Icons.Outlined.Home,
        Icons.AutoMirrored.Outlined.List,
        Icons.Outlined.Person
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, (screen, label) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == screen.route) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = label
                    )
                },
                label = { Text(label, fontSize = 10.sp) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {

                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}