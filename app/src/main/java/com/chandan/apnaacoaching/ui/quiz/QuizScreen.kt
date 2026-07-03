package com.chandan.apnaacoaching.ui.quiz

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

// Standard CBT Colors
val ColorAnswered = Color(0xFF4CAF50)       // Green
val ColorNotAnswered = Color(0xFFF44336)    // Red
val ColorNotVisited = Color(0xFFE0E0E0)     // Gray
val ColorMarked = Color(0xFF9C27B0)         // Purple
val ColorAnsweredMarked = Color(0xFF2196F3) // Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    quizId: Int,
    navController: NavController,
    userId: String,
) {
    val context = LocalContext.current // <-- NEW: Needed for the Toast message
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val answers by viewModel.userAnswers.collectAsState()

    // --- NEW: Observe the live timer ---
    val timeLeft by viewModel.formattedTime.collectAsState()

    // State for Language Toggle (Default: English)
    var isHindi by remember { mutableStateOf(false) }

    val markedQuestions by viewModel.markedQuestions.collectAsState()

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentQue = questions[currentIndex]
    val baseUrl = "https://apnaacoaching.in"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // --- NEW: Replaced hardcoded text with the live StateFlow ---
                        Text(timeLeft, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                actions = {
                    TextButton(onClick = { isHindi = !isHindi }) {
                        Icon(Icons.Default.Language, contentDescription = "Language")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHindi) "ENG" else "हिं", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Action Buttons pinned to the bottom (Matches cbt.php)
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val isCurrentlyMarked = markedQuestions.contains(questions[currentIndex].id)

                        TextButton(onClick = { viewModel.clearAnswer() }) {
                            Text("Clear Response", color = Color.Gray)
                        }
                        TextButton(onClick = { viewModel.toggleMarkForReview() }) {
                            Text(
                                if (isCurrentlyMarked) "Unmark" else "Mark for Review",
                                color = ColorMarked
                            )
                        }
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.previousQuestion() },
                            enabled = currentIndex > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Prev",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Prev")
                        }

                        if (currentIndex == questions.size - 1) {
                            Button(
                                onClick = {
                                    // Trigger the API call
                                    viewModel.submitQuiz(userId, quizId) { isSuccess, message ->
                                        // Show the result from the PHP script
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                                        if (isSuccess) {
                                            // Exit the quiz and return to the previous screen
                                            navController.navigateUp()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorAnswered)
                            ) {
                                Text("Submit Final")
                            }
                        } else {
                            Button(onClick = { viewModel.nextQuestion() }) {
                                Text("Save & Next")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // --- TOP QUESTION PALETTE (Horizontal Scroll) ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(questions.size) { index ->
                    val qId = questions[index].id
                    val isCurrent = index == currentIndex
                    val isAns = answers.containsKey(qId) && answers[qId] != null

                    val isMarked = markedQuestions.contains(qId)

                    // Basic color logic (can be expanded to include Marked status later)
// --- NEW: Advanced Palette Color Logic ---
                    val bgColor = when {
                        isAns && isMarked -> ColorAnsweredMarked // Blue
                        !isAns && isMarked -> ColorMarked        // Purple
                        isAns -> ColorAnswered                   // Green
                        isCurrent -> ColorNotAnswered            // Red
                        else -> ColorNotVisited                  // Gray
                    }
                    val textColor = if (bgColor == ColorNotVisited) Color.Black else Color.White

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .border(
                                width = if (isCurrent) 2.dp else 0.dp,
                                color = if (isCurrent) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { viewModel.goToQuestion(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            HorizontalDivider()

            // --- QUESTION & OPTIONS AREA ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Question Header
                Text(
                    text = "Question ${currentIndex + 1}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Dynamic Question Text (Hindi / English)
                val displayQuestion = if (isHindi) currentQue.que_hi else currentQue.que_En
                Text(
                    text = displayQuestion,
                    style = MaterialTheme.typography.titleLarge,
                    lineHeight = 28.sp
                )

                // Optional Question Image
                val displayImg = if (isHindi) currentQue.que_img_Hi else currentQue.que_img_En
                if (!displayImg.isNullOrEmpty() && displayImg != "/config/image/option/") {
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        model = baseUrl + displayImg,
                        contentDescription = "Question Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Options List
                val optionsList = if (isHindi) currentQue.answer_hi else currentQue.answer_en
                val optionsImgList =
                    if (isHindi) currentQue.answer_img_hi else currentQue.answer_img_en

                optionsList.forEachIndexed { index, optionText ->
                    val optionId = currentQue.answers_id[index]
                    val isSelected = answers[currentQue.id] == optionId

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { viewModel.selectAnswer(currentQue.id, optionId) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.5f
                            ) else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Custom Radio Circle
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                // Option Image (If exists)
                                val optImg = optionsImgList[index]
                                if (!optImg.isNullOrEmpty() && optImg != "/config/image/option/") {
                                    AsyncImage(
                                        model = baseUrl + optImg,
                                        contentDescription = "Option Image",
                                        modifier = Modifier.heightIn(max = 100.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Option Text
                                Text(text = optionText, fontSize = 16.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }

                // Bottom spacing for scroll clearance above the BottomBar
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}