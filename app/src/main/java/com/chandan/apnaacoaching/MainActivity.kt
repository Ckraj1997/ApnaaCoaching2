package com.chandan.apnaacoaching

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chandan.apnaacoaching.ui.DashboardScreen
import com.chandan.apnaacoaching.ui.auth.AuthViewModel
import com.chandan.apnaacoaching.ui.auth.LoginScreen
import com.chandan.apnaacoaching.ui.auth.triggerGoogleSignIn
import com.chandan.apnaacoaching.ui.theme.ApnaaCoachingTheme
import com.chandan.apnaacoaching.utils.SessionManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)

        setContent {
            ApnaaCoachingTheme {

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