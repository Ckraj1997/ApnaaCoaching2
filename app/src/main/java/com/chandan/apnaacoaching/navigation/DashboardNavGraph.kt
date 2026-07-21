package com.chandan.apnaacoaching.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chandan.apnaacoaching.ui.ComingSoonScreen
import com.chandan.apnaacoaching.ui.DashboardScreen
import com.chandan.apnaacoaching.ui.DashboardState
import com.chandan.apnaacoaching.ui.HomeScreen
import com.chandan.apnaacoaching.ui.PracticeScreen
import com.chandan.apnaacoaching.ui.PracticeViewModel
import com.chandan.apnaacoaching.ui.ProfileScreen
import com.chandan.apnaacoaching.ui.ProfileViewModel
import com.chandan.apnaacoaching.ui.categories.CategoryScreen
import com.chandan.apnaacoaching.ui.categories.CategoryViewModel
import com.chandan.apnaacoaching.ui.community.CommentsScreen
import com.chandan.apnaacoaching.ui.community.CommunityGroupsScreen
import com.chandan.apnaacoaching.ui.community.CommunityThreadsScreen
import com.chandan.apnaacoaching.ui.community.CommunityViewModel
import com.chandan.apnaacoaching.ui.community.CreateThreadScreen
import com.chandan.apnaacoaching.ui.community.ThreadRepliesScreen
import com.chandan.apnaacoaching.ui.components.TestListScreen
import com.chandan.apnaacoaching.ui.levels.LevelScreen
import com.chandan.apnaacoaching.ui.levels.LevelViewModel
import com.chandan.apnaacoaching.ui.quiz.DetailedResultScreen
import com.chandan.apnaacoaching.ui.quiz.InstructionsScreen
import com.chandan.apnaacoaching.ui.quiz.QuizScreen
import com.chandan.apnaacoaching.ui.quiz.QuizViewModel
import com.chandan.apnaacoaching.ui.quiz.ResultScreen
import com.chandan.apnaacoaching.ui.quiz.ResultViewModel
import com.chandan.apnaacoaching.ui.studymaterial.StudyMaterialScreen
import com.chandan.apnaacoaching.ui.studymaterial.oneliner.OneLinerScreen
import com.chandan.apnaacoaching.ui.studymaterial.oneliner.OneLinerViewModel
import com.chandan.apnaacoaching.ui.studymaterial.pdf.PdfPlayerScreen
import com.chandan.apnaacoaching.ui.studymaterial.pdf.PdfScreen
import com.chandan.apnaacoaching.ui.studymaterial.pdf.PdfViewModel
import com.chandan.apnaacoaching.ui.studymaterial.quiz.QuizListScreen
import com.chandan.apnaacoaching.ui.studymaterial.quiz.QuizListViewModel
import com.chandan.apnaacoaching.ui.studymaterial.subjective.SubjectiveScreen
import com.chandan.apnaacoaching.ui.studymaterial.subjective.SubjectiveViewModel
import com.chandan.apnaacoaching.ui.studymaterial.update.UpdateScreen
import com.chandan.apnaacoaching.ui.studymaterial.update.UpdateViewModel
import com.chandan.apnaacoaching.ui.studymaterial.video.VideoPlayerScreen
import com.chandan.apnaacoaching.ui.studymaterial.video.VideoScreen
import com.chandan.apnaacoaching.ui.studymaterial.video.VideoViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import com.chandan.apnaacoaching.ui.EditProfileScreen
import com.chandan.apnaacoaching.ui.SearchScreen
import com.chandan.apnaacoaching.ui.SearchViewModel
import com.chandan.apnaacoaching.utils.SessionManager

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
                onRetry = onRetry,
                navController = navController,
            )
        }

        composable(Screen.Community.route) {
            // Initialize the ViewModel here or pass it if you are using Hilt/Dagger
            val communityViewModel: CommunityViewModel = viewModel()

            CommunityGroupsScreen(
                navController = navController,
                viewModel = communityViewModel
            )
        }
        composable(Screen.Content.route) {
            ComingSoonScreen(featureName = "My Content")
        }
        composable(Screen.Settings.route) {
            ComingSoonScreen(featureName = "Settings")
        }

        composable(Screen.Practice.route) {

            LaunchedEffect(Unit) { practiceViewModel.fetchTests(userId) }

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

        composable("instructions/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")?.toInt() ?: 0

            val liveTests by practiceViewModel.liveTests.collectAsState()
            val mockTests by practiceViewModel.mockTests.collectAsState()

            val liveTest = liveTests.find { it.id == quizId }
            val mockTest = mockTests.find { it.id == quizId }

            val testHeading = liveTest?.heading ?: mockTest?.heading ?: "Assessment"

            InstructionsScreen(
                quizId = quizId,
                heading = testHeading, // <-- FIX: We are now passing the heading
                navController = navController
            )
        }

        composable("quiz_screen/{quizId}?lang={lang}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")?.toInt() ?: 0

            val lang = backStackEntry.arguments?.getString("lang") ?: "en"

            val liveTests by practiceViewModel.liveTests.collectAsState()
            val mockTests by practiceViewModel.mockTests.collectAsState()

            val liveTest = liveTests.find { it.id == quizId }
            val mockTest = mockTests.find { it.id == quizId }

            val testDuration = liveTest?.timeDuration ?: mockTest?.timeDuration ?: 90

            val viewModel: QuizViewModel = viewModel()

            LaunchedEffect(quizId) {
                viewModel.fetchQuestions(quizId, userId, testDuration)
            }

            QuizScreen(
                viewModel = viewModel,
                quizId = quizId,
                userId = userId,
                navController = navController,
                initialLang = lang // <-- FIX: We are now passing the initial language
            )
        }

        composable(Screen.Account.route) {
            ProfileScreen(
                userId = userId, // Pass the dynamic user ID you passed into the graph
                viewModel = profileViewModel,
                navController = navController
            )
        }

        composable("edit_profile") {
            // Use the profileViewModel that was already declared at the top of DashboardNavGraph!
            EditProfileScreen(
                viewModel = profileViewModel,
                navController = navController
            )
        }
        composable("result_screen/{testType}/{correct}/{wrong}/{skipped}/{total}") { backStackEntry ->
            val testType = backStackEntry.arguments?.getString("testType") ?: "mock"
            val correct = backStackEntry.arguments?.getString("correct")?.toInt() ?: 0
            val wrong = backStackEntry.arguments?.getString("wrong")?.toInt() ?: 0
            val skipped = backStackEntry.arguments?.getString("skipped")?.toInt() ?: 0
            val total = backStackEntry.arguments?.getString("total")?.toInt() ?: 0

            ResultScreen(
                navController = navController,
                testType = testType,
                totalQuestions = total,
                correctCount = correct,
                wrongCount = wrong,
                unattemptedCount = skipped
            )
        }

        composable("detailed_result_screen/{quizId}/{userId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")?.toInt() ?: 0
            val routeUserId = backStackEntry.arguments?.getString("userId") ?: userId

            val resultViewModel: ResultViewModel = viewModel()

            DetailedResultScreen(
                quizId = quizId,
                userId = routeUserId,
                viewModel = resultViewModel,
                navController = navController
            )
        }

        composable("level_screen/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: "0"
            val levelViewModel: LevelViewModel = viewModel()

            LevelScreen(
                groupId = groupId,
                viewModel = levelViewModel,
                navController = navController
            )
        }

        // Inside your NavHost block...
        composable("category_screen/{groupId}/{levelId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: "0"
            val levelId = backStackEntry.arguments?.getString("levelId") ?: "0"
            val categoryViewModel: CategoryViewModel = viewModel()

            CategoryScreen(
                groupId = groupId,
                levelId = levelId,
                viewModel = categoryViewModel,
                navController = navController
            )
        }

        // --- ADD THIS INSIDE YOUR NavHost ---
        composable("study_material_screen/{groupId}/{levelId}/{categoryId}") { backStackEntry ->
            // Extract the IDs from the route
            val groupId = backStackEntry.arguments?.getString("groupId") ?: "0"
            val levelId = backStackEntry.arguments?.getString("levelId") ?: "0"
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "0"

            // Call the screen
            StudyMaterialScreen(
                groupId = groupId,
                levelId = levelId,
                categoryId = categoryId,
                navController = navController
            )
        }

        // The new One-Liner Route
        composable("one_liner_screen/{groupId}/{levelId}/{categoryId}") { backStackEntry ->

            // 1. Extract the IDs from the route
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val levelId = backStackEntry.arguments?.getString("levelId") ?: ""
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""

            // 2. Pass them to the screen (assuming you instantiate your ViewModel here)
            val oneLinerViewModel: OneLinerViewModel = viewModel() // or hiltViewModel()
            OneLinerScreen(
                groupId = groupId,
                levelId = levelId,
                catId = categoryId,
                viewModel = oneLinerViewModel,
                navController = navController
            )
        }

        composable("subjective_screen/{groupId}/{levelId}/{categoryId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val levelId = backStackEntry.arguments?.getString("levelId") ?: ""
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""

            val subjectiveViewModel: SubjectiveViewModel = viewModel()
            SubjectiveScreen(
                groupId = groupId,
                levelId = levelId,
                catId = categoryId,
                viewModel = subjectiveViewModel, // pass your initialized viewmodel here
                navController = navController
            )
        }

        composable("quiz_list_screen/{groupId}/{levelId}/{categoryId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val levelId = backStackEntry.arguments?.getString("levelId") ?: ""
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""

            val quizListViewModel: QuizListViewModel = viewModel()
            QuizListScreen(
                groupId = groupId,
                levelId = levelId,
                catId = categoryId,
                viewModel = quizListViewModel, // pass your initialized viewmodel here
                navController = navController,
                userId = userId
            )
        }

        composable("video_screen/{groupId}/{levelId}/{categoryId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val levelId = backStackEntry.arguments?.getString("levelId") ?: ""
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""

            val videoViewModel: VideoViewModel = viewModel()
            VideoScreen(
                groupId = groupId,
                levelId = levelId,
                catId = categoryId,
                viewModel = videoViewModel,
                navController = navController
            )
        }

        composable("pdf_screen/{groupId}/{levelId}/{categoryId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val levelId = backStackEntry.arguments?.getString("levelId") ?: ""
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""

            val pdfViewModel: PdfViewModel = viewModel()
            PdfScreen(
                groupId = groupId,
                levelId = levelId,
                catId = categoryId,
                viewModel = pdfViewModel,
                navController = navController
            )
        }

        composable("update_screen/{groupId}/{levelId}/{categoryId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val levelId = backStackEntry.arguments?.getString("levelId") ?: ""
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""

            val updateViewModel: UpdateViewModel = viewModel()
            UpdateScreen(
                groupId = groupId,
                levelId = levelId,
                catId = categoryId,
                viewModel = updateViewModel,
                navController = navController
            )
        }

        composable("video_player_screen/{videoId}") { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""

            VideoPlayerScreen(
                videoId = videoId, // Passing the clean ID directly
                navController = navController
            )
        }

        composable("pdf_player_screen/{encodedUrl}/{pdfTitle}") { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("encodedUrl") ?: ""
            val pdfTitle = backStackEntry.arguments?.getString("pdfTitle") ?: "Document"

            PdfPlayerScreen(
                encodedUrl = encodedUrl,
                pdfTitle = pdfTitle,
                navController = navController
            )
        }

        composable("create_thread_screen/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val communityViewModel: CommunityViewModel = viewModel()
            CreateThreadScreen(
                groupId = groupId,
                userId = userId,
                navController = navController,
                viewModel = communityViewModel
            )
        }

        composable("replies_screen/{threadId}") { backStackEntry ->
            val threadId = backStackEntry.arguments?.getString("threadId") ?: ""
            val communityViewModel: CommunityViewModel = viewModel()
            ThreadRepliesScreen(
                threadId = threadId,
                userId = userId,
                navController = navController,
                viewModel = communityViewModel
            )
        }

        // 1. The Entry Point (Groups Screen)
        composable("community_groups_screen") {
            val communityViewModel: CommunityViewModel = viewModel()
            CommunityGroupsScreen(
                navController = navController,
                viewModel = communityViewModel
            )
        }

        // 2. The Threads Screen (Update your existing composable to match this name)
        composable("community_threads_screen/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val communityViewModel: CommunityViewModel = viewModel()

            CommunityThreadsScreen(
                groupId = groupId,
                navController = navController,
                viewModel = communityViewModel
            )
        }

        composable("comments_screen/{threadId}/{replyId}") { backStackEntry ->
            val threadId = backStackEntry.arguments?.getString("threadId") ?: ""
            val replyId = backStackEntry.arguments?.getString("replyId") ?: ""
            val communityViewModel: CommunityViewModel = viewModel()

            CommentsScreen(
                threadId = threadId,
                replyId = replyId,
                userId = userId,
                navController = navController,
                viewModel = communityViewModel
            )
        }

        composable("search_screen") {
            val searchViewModel: SearchViewModel = viewModel()
            SearchScreen(
                viewModel = searchViewModel,
                navController = navController
            )
        }
    }
}