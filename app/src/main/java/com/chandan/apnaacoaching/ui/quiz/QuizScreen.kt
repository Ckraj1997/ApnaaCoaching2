package com.chandan.apnaacoaching.ui.quiz

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

val ColorAnswered = Color(0xFF4CAF50) // Green
val ColorNotAnswered = Color(0xFFF44336) // Red
val ColorNotVisited = Color(0xFFE0E0E0) // Gray
val ColorMarked = Color(0xFF9C27B0) // Purple
val ColorAnsweredMarked = Color(0xFF2196F3) // Blue

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    quizId: Int,
    navController: NavController,
    userId: String,
    initialLang: String
) {
    val context = LocalContext.current // <-- NEW: Needed for the Toast message
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val answers by viewModel.userAnswers.collectAsState()

    val timeLeft by viewModel.formattedTime.collectAsState()

    var isHindi by remember { mutableStateOf(initialLang == "hi") }

    val markedQuestions by viewModel.markedQuestions.collectAsState()

    var showSubmitDialog by remember { mutableStateOf(false) }

    val isAutoSubmitted by viewModel.isAutoSubmitted.collectAsState()

    val visitedQuestions by viewModel.visitedQuestions.collectAsState()

    LaunchedEffect(currentIndex) {
        if (questions.isNotEmpty()) {
            viewModel.markQuestionAsVisited(questions[currentIndex].id)
        }
    }

    LaunchedEffect(isAutoSubmitted) {
        if (isAutoSubmitted) {
            Toast.makeText(context, "Time's up! Exam auto-submitted.", Toast.LENGTH_LONG).show()

            navController.navigate("detailed_result_screen/$quizId/$userId") {
                popUpTo("quiz_screen/$quizId") { inclusive = true }
            }
        }
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (showSubmitDialog) {
        val totalQuestions = questions.size
        var answered = 0
        var notAnswered = 0
        var marked = 0
        var answeredMarked = 0
        var notVisited = 0

        for (i in 0 until totalQuestions) {
            val qId = questions[i].id
            val isAns = answers.containsKey(qId) && answers[qId] != null
            val isMarked = markedQuestions.contains(qId)

            when {
                isAns && isMarked -> answeredMarked++
                !isAns && isMarked -> marked++
                isAns && !isMarked -> answered++
                else -> notVisited++ // Adjust this if you track "visited but not answered"
            }
        }

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Exam Summary", fontWeight = FontWeight.Bold) },
            text = {
                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Questions:", fontWeight = FontWeight.Bold)
                        Text("$totalQuestions")
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Answered:", color = ColorAnswered)
                        Text("$answered")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Not Answered:", color = ColorNotAnswered)
                        Text("$notAnswered") // Update when visited logic is added
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Marked for Review:", color = ColorMarked)
                        Text("$marked")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Answered & Marked:", color = ColorAnsweredMarked)
                        Text("$answeredMarked")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Not Visited:", color = Color.Gray)
                        Text("$notVisited")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Are you sure you want to submit for final marking? No changes will be allowed after submission.",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false

                        viewModel.submitQuiz(userId, quizId) { isSuccess, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            if (isSuccess) {

                                navController.navigate("detailed_result_screen/$quizId/$userId") {

                                    popUpTo("quiz_screen/$quizId") { inclusive = true }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("YES")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showSubmitDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("NO")
                }
            }
        )
    }

    val currentQue = questions[currentIndex]
    val baseUrl = "https://apnaacoaching.in"

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        modifier = Modifier.weight(1f), // Takes up available space
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(timeLeft, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    TextButton(onClick = { isHindi = !isHindi }) {
                        Icon(Icons.Default.Language, contentDescription = "Language")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHindi) "ENG" else "हिं", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        bottomBar = {

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
                                onClick = { showSubmitDialog = true }, // SHOW DIALOG HERE
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
                .fillMaxSize()
                .padding(top = 56.dp)
                .background(Color(0xFFF8F9FA))
        ) {

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(questions.size) { index ->
                    val qId = questions[index].id
                    val isCurrent = index == currentIndex
                    val isAns = answers.containsKey(qId) && answers[qId] != null

                    val isMarked = markedQuestions.contains(qId)
                    val isVisited = visitedQuestions.contains(qId)

                    val bgColor = when {
                        isAns && isMarked -> ColorAnsweredMarked // Blue
                        !isAns && isMarked -> ColorMarked // Purple
                        isAns -> ColorAnswered // Green
                        isCurrent -> ColorNotAnswered
                        isVisited -> ColorNotAnswered// Red
                        else -> ColorNotVisited // Gray
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                Text(
                    text = "Question ${currentIndex + 1}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                val displayQuestion = if (isHindi) currentQue.que_hi else currentQue.que_En
                Text(
                    text = displayQuestion,
                    style = MaterialTheme.typography.titleLarge,
                    lineHeight = 28.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )

                val displayImg = if (isHindi) currentQue.que_img_Hi else currentQue.que_img_En
                if (!displayImg.isNullOrEmpty() && displayImg != "/config/image/option/" && displayImg != "/config/image/option/image.hindi") {
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
                            verticalAlignment = Alignment.Top
                        ) {

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

                                val optImg = optionsImgList[index]
                                if (!optImg.isNullOrEmpty() && optImg != "/config/image/option/" && optImg != "/config/image/option/image.hindi") {
                                    AsyncImage(
                                        model = baseUrl + optImg,
                                        contentDescription = "Option Image",
                                        modifier = Modifier.heightIn(max = 100.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Text(text = optionText, fontSize = 16.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}