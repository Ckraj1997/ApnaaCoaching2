package com.chandan.apnaacoaching.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chandan.apnaacoaching.ui.ComingSoonScreen
import com.chandan.apnaacoaching.ui.DashboardState
import com.chandan.apnaacoaching.ui.HomeScreen
import com.chandan.apnaacoaching.ui.PracticeScreen
import com.chandan.apnaacoaching.ui.PracticeViewModel
import com.chandan.apnaacoaching.ui.ProfileScreen
import com.chandan.apnaacoaching.ui.ProfileViewModel
import com.chandan.apnaacoaching.ui.components.TestListScreen
import com.chandan.apnaacoaching.ui.quiz.InstructionsScreen
import com.chandan.apnaacoaching.ui.quiz.QuizScreen
import com.chandan.apnaacoaching.ui.quiz.QuizViewModel

@Composable
fun DashboardNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userName: String,
    userId: String,
    uiState: DashboardState,
    onRetry: () -> Unit
) {

    val practiceViewModel: PracticeViewModel = viewModel()

    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                userName = userName,
                uiState = uiState,
                onRetry = onRetry
            )
        }
//        composable(Screen.Practice.route) {
//            PracticeScreen()
//        }
        composable(Screen.Cart.route) {
            ComingSoonScreen(featureName = "My Cart")
        }
        composable(Screen.Content.route) {
            ComingSoonScreen(featureName = "My Content")
        }
        composable(Screen.Account.route) {
            ComingSoonScreen(featureName = "User Account")
        }
        composable(Screen.Settings.route) {
            ComingSoonScreen(featureName = "Settings")
        }

        composable(Screen.Practice.route) {
            // Trigger the fetch when the user opens the practice screen
            LaunchedEffect(Unit) { practiceViewModel.fetchTests(userId) }

            // Pass navController so the tiles can click through
            PracticeScreen(navController = navController)
        }

        composable("test_list/{testType}") { backStackEntry ->
            val testType = backStackEntry.arguments?.getString("testType") ?: "mock"
            TestListScreen(
                testType = testType,
                viewModel = practiceViewModel,
                navController = navController,
                userId = userId
            )
        }

        // --- ADD THIS BLOCK ---
        composable("instructions/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")?.toInt() ?: 0

            InstructionsScreen(
                quizId = quizId,
                navController = navController
            )
        }
        // ----------------------

        composable("quiz_screen/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")?.toInt() ?: 0
            val viewModel: QuizViewModel = viewModel()

            LaunchedEffect(quizId) {
                viewModel.fetchQuestions(quizId, userId)
            }

            QuizScreen(
                viewModel = viewModel,
                quizId = quizId,
                userId = userId, // <-- NEW: Pass the userId down
                navController = navController
            )
        }

        composable(Screen.Account.route) {
            ProfileScreen(
                userId = userId, // Pass the dynamic user ID you passed into the graph
                viewModel = profileViewModel,
                navController = navController
            )
        }
    }
}