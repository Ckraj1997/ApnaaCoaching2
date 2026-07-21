package com.chandan.apnaacoaching

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.chandan.apnaacoaching.ui.settings.LanguageViewModel
import com.chandan.apnaacoaching.ui.theme.ApnaaCoachingTheme
import com.chandan.apnaacoaching.ui.theme.ThemeViewModel
import com.chandan.apnaacoaching.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.Locale

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
            val languageViewModel: LanguageViewModel = viewModel()
            val isHindi by languageViewModel.isHindi.collectAsState()

            // 1. Determine the language code
            val localeString = if (isHindi) "hi" else "en"
            val locale = Locale(localeString)

            // 2. Update the system configuration
            val configuration = LocalConfiguration.current
            configuration.setLocale(locale)

            // 3. Create an updated context with the new language
            val context = LocalContext.current
            val updatedContext = context.createConfigurationContext(configuration)

            // ---> NEW: CAPTURE THE ACTIVITY REGISTRY OWNER <---
            val registryOwner = LocalActivityResultRegistryOwner.current
            // 4. Wrap your app to provide this new language context everywhere
            CompositionLocalProvider(
                LocalContext provides updatedContext,
                LocalConfiguration provides configuration,
                LocalActivityResultRegistryOwner provides registryOwner!!
            ){
                val themeViewModel: ThemeViewModel = viewModel()
                val isDark by themeViewModel.isDarkMode.collectAsState()
                ApnaaCoachingTheme(darkTheme = isDark) {

                    var loggedInUserId by remember { mutableStateOf(sessionManager.getUserId()) }
                    var loggedInUserName by remember { mutableStateOf(sessionManager.getUserName()) }

                    if (loggedInUserId != null && loggedInUserName != null) {

                        DashboardScreen(
                            userName = loggedInUserName!!,
                            userId = loggedInUserId!!,
                            onLogout = {
                                // 1. Clear the data from SharedPreferences
                                sessionManager.clearSession()

                                // 2. Set states to null to instantly trigger the LoginScreen
                                loggedInUserId = null
                                loggedInUserName = null
                            }
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
                                                getString(R.string.google_sign_in_cancelled_failed),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                onNavigateToSignUp = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.navigating_to_sign_up),
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
}