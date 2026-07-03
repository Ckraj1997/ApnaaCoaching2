package com.chandan.apnaacoaching.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.chandan.apnaacoaching.navigation.DashboardNavGraph
import com.chandan.apnaacoaching.navigation.Screen
import com.chandan.apnaacoaching.ui.components.DashboardBottomNav
import com.chandan.apnaacoaching.ui.components.DashboardTopBar
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    userName: String,
    userId: String,
    viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // 1. Initialize the Navigation Controller
    val navController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 2. Observe the Live Data from Hostinger
    val uiState by viewModel.uiState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "ApnaaCoaching",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("My Profile") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Account.route) // Navigate via route
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Settings.route) // Navigate via route
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    },
                    label = { Text("Logout") },
                    selected = false,
                    onClick = { /* TODO: Trigger Logout ViewModel */ },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                DashboardTopBar(onOpenDrawer = { scope.launch { drawerState.open() } })
            },
            bottomBar = {
                DashboardBottomNav(navController = navController)
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->

            // 3. The NavGraph handles everything else seamlessly!
            DashboardNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                userName = userName,
                userId = userId,
                uiState = uiState,
                onRetry = { viewModel.fetchDashboardData() }
            )

        }
    }
}