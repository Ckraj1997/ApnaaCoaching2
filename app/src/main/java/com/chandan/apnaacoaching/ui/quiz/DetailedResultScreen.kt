package com.chandan.apnaacoaching.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.chandan.apnaacoaching.ui.components.RankingTab
import com.chandan.apnaacoaching.ui.components.SolutionsTab
import com.chandan.apnaacoaching.ui.components.StatsTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedResultScreen(
    quizId: Int,
    userId: String,
    viewModel: ResultViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Stats", "Ranking", "Solutions")

    LaunchedEffect(quizId) {
        viewModel.fetchDetailedResult(quizId, userId)
    }

    val exitToDashboard = {
        navController.navigate("practice") { // Replace "practice" with your actual main list/dashboard route

            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
            }
        }
    }

    BackHandler {
        exitToDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exam Report", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {

            Surface(shadowElevation = 8.dp, color = Color.White) {
                Button(
                    onClick = exitToDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Back to Test List")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            when (val state = uiState) {
                is ResultUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ResultUiState.Error -> {
                    Text(
                        state.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ResultUiState.Awaiting -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Results Awaiting", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Will be released at: ${state.releaseTime}", color = Color.Gray)
                    }
                }

                is ResultUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {

                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(title, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }

                        val resultData = state.data
                        when (selectedTabIndex) {

                            0 -> StatsTab(resultData.stats, resultData.test_config)
                            1 -> RankingTab(resultData.ranking, userId)
                            2 -> SolutionsTab(resultData.details)
                        }
                    }
                }
            }
        }
    }
}