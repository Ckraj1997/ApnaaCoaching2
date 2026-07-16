package com.chandan.apnaacoaching.ui.studymaterial.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chandan.apnaacoaching.data.QuizItem

@Composable
fun QuizListScreen(
    userId: String,
    groupId: String,
    levelId: String,
    catId: String,
    viewModel: QuizListViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(groupId, levelId, catId) {
        viewModel.fetchQuizList(userId, groupId, levelId, catId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- CUSTOM TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)
                    .background(Color.White)
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quizzes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            // --- MAIN LIST AREA ---
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (val state = uiState) {
                    is QuizListUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is QuizListUiState.Error -> {
                        Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    }
                    is QuizListUiState.Success -> {
                        if (state.quizzes.isEmpty()) {
                            Text("No quizzes available.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.quizzes) { quiz ->
                                    StaticQuizCard(
                                        quiz = quiz,
                                        userId = userId,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StaticQuizCard(
    quiz: QuizItem,
    userId: String,
    navController: NavController
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Title & Description
            Text(
                text = quiz.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            if (!quiz.description.isNullOrEmpty()) {
                Text(
                    text = quiz.description,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Marks / Points Info (CBT Style)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Quiz,
                    contentDescription = "Points",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Marks: +${quiz.pluspoints} | -${quiz.minuspoints}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Buttons based on isPlayed state
            if (quiz.isPlayed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("quiz_player_screen/${quiz.quiz_id}") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reattempt")
                    }
                    Button(
                        onClick = {
                            // Assuming you have a detailed result screen similar to CBT
                            navController.navigate("detailed_result_screen/${quiz.quiz_id}/$userId")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("View Result")
                    }
                }
            } else {
                Button(
                    onClick = {
                        // Add enrollment/payment logic here if entry_fee > 0, otherwise just start
                        navController.navigate("quiz_player_screen/${quiz.quiz_id}")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (quiz.entry_fee > 0) "Play for ${quiz.entry_fee} Coins" else "Start Quiz")
                }
            }
        }
    }
}