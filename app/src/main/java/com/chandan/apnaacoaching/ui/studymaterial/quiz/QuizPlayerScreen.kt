//package com.chandan.apnaacoaching.ui.studymaterial.quiz
//




package com.chandan.apnaacoaching.ui.studymaterial.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayerScreen(
    quizId: String,
    viewModel: QuizPlayerViewModel,
    navController: NavController,
    userId: String,
    defaultToHindi: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()

    // 1. Add Local State for Language Toggle
    var isHindi by remember { mutableStateOf(defaultToHindi) }

    LaunchedEffect(quizId) {
        viewModel.loadStudyMaterialQuiz(quizId)
    }

    LaunchedEffect(uiState) {
        if (uiState is QuizPlayerUiState.Success && (uiState as QuizPlayerUiState.Success).isFinished) {
            viewModel.resetFinishedState()

            // 2. Fix Navigation: Pop the player from the back stack first, THEN navigate to results.
            // This ensures when they press 'back' on the results, they return to the Quiz List!
            navController.popBackStack()
            navController.navigate("study_quiz_solution_screen/$quizId/$userId")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Study Quiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // 3. Add the Language Toggle Button to the Top Bar
                actions = {
                    TextButton(onClick = { isHindi = !isHindi }) {
                        Text(
                            text = if (isHindi) "HI" else "EN",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )

            HorizontalDivider()

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (val state = uiState) {
                    is QuizPlayerUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is QuizPlayerUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                    is QuizPlayerUiState.Success -> {
                        val currentQ = state.questions[state.currentIndex]
                        val selectedOptId = state.selectedAnswers[currentQ.questionId]

                        // Determine text based on the toggle state
                        val questionText = if (isHindi && !currentQ.questionHi.isNullOrBlank()) currentQ.questionHi else currentQ.questionEn

                        Column(
                            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
                        ) {
                            Text(
                                text = "Question ${state.currentIndex + 1} of ${state.questions.size}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LinearProgressIndicator(
                                progress = { (state.currentIndex + 1) / state.questions.size.toFloat() },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = questionText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            if (!currentQ.questionImage.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                AsyncImage(
                                    model = "https://apnaacoaching.in/config/image/level/${currentQ.questionImage}",
                                    contentDescription = "Question Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).clip(RoundedCornerShape(8.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            currentQ.options.forEach { option ->
                                val optText = if (isHindi && !option.optionTextHi.isNullOrBlank()) option.optionTextHi else option.optionTextEn

                                OptionRow(
                                    text = optText,
                                    imageUrl = option.optionImage,
                                    isSelected = option.optionId == selectedOptId,
                                    onClick = { viewModel.selectOption(currentQ.questionId, option.optionId) }
                                )
                            }
                        }
                    }
                }
            }

            // BOTTOM BAR
            if (uiState is QuizPlayerUiState.Success) {
                val state = uiState as QuizPlayerUiState.Success
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.previousQuestion() },
                            enabled = state.currentIndex > 0 && !state.isSubmitting,
                            modifier = Modifier.weight(1f)
                        ) { Text("Previous") }

                        Spacer(modifier = Modifier.width(16.dp))

                        if (state.currentIndex < state.questions.size - 1) {
                            Button(onClick = { viewModel.nextQuestion() }, modifier = Modifier.weight(1f)) { Text("Next") }
                        } else {
                            Button(
                                onClick = { viewModel.submitQuiz(quizId, userId) },
                                enabled = !state.isSubmitting,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                if (state.isSubmitting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                else Text("Submit Quiz")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionRow(text: String, imageUrl: String?, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isSelected, onClick = null)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = text, style = MaterialTheme.typography.bodyLarge)
                if (!imageUrl.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = "https://apnaacoaching.in/config/image/level/$imageUrl",
                        contentDescription = "Option",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 120.dp).clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import coil.compose.AsyncImage
//import com.chandan.apnaacoaching.data.StudyQuizOption
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun QuizPlayerScreen(
//    quizId: String,
//    viewModel: QuizPlayerViewModel,
//    navController: NavController,
//    userId: String,
//    isHindiPreferred: Boolean = false // Set this based on your language state
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    LaunchedEffect(quizId) {
//        viewModel.loadStudyMaterialQuiz(quizId)
//    }
//
//    LaunchedEffect(uiState) {
//        if (uiState is QuizPlayerUiState.Success && (uiState as QuizPlayerUiState.Success).isFinished) {
//            viewModel.resetFinishedState()
//            // Redirect to the dedicated solution screen
//            navController.navigate("study_quiz_solution_screen/$quizId/$userId") {
//                popUpTo("quiz_player_screen/$quizId") { inclusive = true }
//            }
//        }
//    }
//
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            TopAppBar(
//                title = { Text("Study Quiz", fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
//            )
//
//            HorizontalDivider()
//
//            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
//                when (val state = uiState) {
//                    is QuizPlayerUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                    is QuizPlayerUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
//                    is QuizPlayerUiState.Success -> {
//                        val currentQ = state.questions[state.currentIndex]
//                        val selectedOptId = state.selectedAnswers[currentQ.questionId]
//
//                        // Determine text based on language preference
//                        val questionText = if (isHindiPreferred && !currentQ.questionHi.isNullOrBlank()) currentQ.questionHi else currentQ.questionEn
//
//                        Column(
//                            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
//                        ) {
//                            Text(
//                                text = "Question ${state.currentIndex + 1} of ${state.questions.size}",
//                                color = MaterialTheme.colorScheme.primary,
//                                fontWeight = FontWeight.SemiBold,
//                                modifier = Modifier.padding(bottom = 8.dp)
//                            )
//                            LinearProgressIndicator(
//                                progress = { (state.currentIndex + 1) / state.questions.size.toFloat() },
//                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
//                            )
//                            Spacer(modifier = Modifier.height(24.dp))
//
//                            Text(
//                                text = questionText,
//                                style = MaterialTheme.typography.titleLarge,
//                                fontWeight = FontWeight.Bold
//                            )
//
//                            if (!currentQ.questionImage.isNullOrEmpty()) {
//                                Spacer(modifier = Modifier.height(16.dp))
//                                AsyncImage(
//                                    model = "https://apnaacoaching.in/config/image/level/${currentQ.questionImage}",
//                                    contentDescription = "Question Image",
//                                    contentScale = ContentScale.Fit,
//                                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).clip(RoundedCornerShape(8.dp))
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.height(24.dp))
//
//                            currentQ.options.forEach { option ->
//                                val optText = if (isHindiPreferred && !option.optionTextHi.isNullOrBlank()) option.optionTextHi else option.optionTextEn
//
//                                OptionRow(
//                                    text = optText,
//                                    imageUrl = option.optionImage,
//                                    isSelected = option.optionId == selectedOptId,
//                                    onClick = { viewModel.selectOption(currentQ.questionId, option.optionId) }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//
//            // BOTTOM BAR
//            if (uiState is QuizPlayerUiState.Success) {
//                val state = uiState as QuizPlayerUiState.Success
//                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth().padding(16.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        OutlinedButton(
//                            onClick = { viewModel.previousQuestion() },
//                            enabled = state.currentIndex > 0 && !state.isSubmitting,
//                            modifier = Modifier.weight(1f)
//                        ) { Text("Previous") }
//
//                        Spacer(modifier = Modifier.width(16.dp))
//
//                        if (state.currentIndex < state.questions.size - 1) {
//                            Button(onClick = { viewModel.nextQuestion() }, modifier = Modifier.weight(1f)) { Text("Next") }
//                        } else {
//                            Button(
//                                onClick = { viewModel.submitQuiz(quizId, userId) },
//                                enabled = !state.isSubmitting,
//                                modifier = Modifier.weight(1f),
//                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
//                            ) {
//                                if (state.isSubmitting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
//                                else Text("Submit Quiz")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun OptionRow(text: String, imageUrl: String?, isSelected: Boolean, onClick: () -> Unit) {
//    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
//    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
//
//    Card(
//        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() },
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = bgColor),
//        border = BorderStroke(1.dp, borderColor)
//    ) {
//        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
//            RadioButton(selected = isSelected, onClick = null)
//            Spacer(modifier = Modifier.width(12.dp))
//            Column {
//                Text(text = text, style = MaterialTheme.typography.bodyLarge)
//                if (!imageUrl.isNullOrEmpty()) {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    AsyncImage(
//                        model = "https://apnaacoaching.in/config/image/level/$imageUrl",
//                        contentDescription = "Option",
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier.fillMaxWidth().heightIn(max = 120.dp).clip(RoundedCornerShape(8.dp))
//                    )
//                }
//            }
//        }
//    }
//}