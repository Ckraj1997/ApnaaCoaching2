package com.chandan.apnaacoaching

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chandan.apnaacoaching.ui.DashboardScreen
import com.chandan.apnaacoaching.ui.auth.AuthViewModel
import com.chandan.apnaacoaching.ui.auth.LoginScreen
import com.chandan.apnaacoaching.ui.auth.triggerGoogleSignIn
import com.chandan.apnaacoaching.ui.theme.ApnaaCoachingTheme
import com.chandan.apnaacoaching.ui.theme.ThemeViewModel
import com.chandan.apnaacoaching.utils.SessionManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        // 1. Tell the app to lay out full-screen (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2. Get the Window Insets Controller
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        // 3. Set the behavior to show bars temporarily when swiping from the edges
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 4. Hide both the status bar and the navigation bar
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        val sessionManager = SessionManager(this)


        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDark by themeViewModel.isDarkMode.collectAsState()
            ApnaaCoachingTheme(darkTheme = isDark) {

                var loggedInUserId by remember { mutableStateOf(sessionManager.getUserId()) }
                var loggedInUserName by remember { mutableStateOf(sessionManager.getUserName()) }

                if (loggedInUserId != null && loggedInUserName != null) {

                    DashboardScreen(
                        userName = loggedInUserName!!,
                        userId = loggedInUserId!!
                    )

                } else {

                    val authViewModel: AuthViewModel = viewModel()

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        LoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = authViewModel,
                            onGoogleSignInClick = {
                                lifecycleScope.launch {
                                    val idToken = triggerGoogleSignIn(this@MainActivity)
                                    if (idToken != null) {
                                        authViewModel.loginWithGoogleToken(idToken)
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Google Sign-In Cancelled/Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            onNavigateToSignUp = {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Navigating to Sign Up...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onLoginSuccess = {

                                loggedInUserId = sessionManager.getUserId()
                                loggedInUserName = sessionManager.getUserName()
                            }
                        )
                    }
                }
            }
        }
    }
}