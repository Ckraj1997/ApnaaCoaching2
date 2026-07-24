package com.chandan.apnaacoaching.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Language
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chandan.apnaacoaching.R
import com.chandan.apnaacoaching.navigation.DashboardNavGraph
import com.chandan.apnaacoaching.navigation.Screen
import com.chandan.apnaacoaching.ui.components.DashboardBottomNav
import com.chandan.apnaacoaching.ui.components.DashboardTopBar
import com.chandan.apnaacoaching.ui.settings.LanguageViewModel
import com.chandan.apnaacoaching.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    userName: String,
    userId: String,
    viewModel: DashboardViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    languageViewModel : LanguageViewModel = viewModel(),
    onLogout: () -> Unit,
//    navController: NavController
) {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()

    val hideBottomBar = currentRoute?.startsWith("instructions") == true ||
            currentRoute?.startsWith("quiz_screen") == true ||
            currentRoute?.startsWith("result_screen") == true || currentRoute?.startsWith("kbc_screen") == true


    val isDark by themeViewModel.isDarkMode.collectAsState()
    val isHindi by languageViewModel.isHindi.collectAsState()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text(stringResource(R.string.my_profile)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Account.route) // Navigate via route
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Settings.route) // Navigate via route
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(id = R.string.logout))
                    },
                    label = { Text(stringResource(id = R.string.logout)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout() // <-- TRIGGER THE CALLBACK HERE
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (isDark) Icons.Default.Brightness2 else Icons.Default.Brightness5,
                            contentDescription = "Theme Toggle"
                        )
                    },
                    label = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isDark) stringResource(R.string.dark_mode) else stringResource(
                                R.string.light_mode
                            ))
                            Switch(
                                checked = isDark,
                                onCheckedChange = { themeViewModel.toggleTheme(it) }
                            )
                        }
                    },
                    selected = false,
                    onClick = { themeViewModel.toggleTheme(!isDark) }, // Toggle on click too
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Language, contentDescription = "Language") },
                    label = { Text("English / हिंदी") },
                    selected = false,
                    onClick = {
                        // Just toggle the boolean!
                        languageViewModel.toggleLanguage(!isHindi)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                DashboardTopBar(onOpenDrawer = { scope.launch { drawerState.open() } },navController)
            },
            bottomBar = {
                if (!hideBottomBar) {
                    DashboardBottomNav(navController = navController)
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->

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