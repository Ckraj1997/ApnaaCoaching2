//package com.chandan.apnaacoaching.ui.studymaterial.quiz
//


package com.chandan.apnaacoaching.ui.studymaterial.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyQuizSolutionScreen(
    quizId: String,
    userId: String,
    viewModel: StudyQuizSolutionViewModel,
    navController: NavController,
    defaultToHindi: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()

    // 1. Add Local State for Language Toggle
    var isHindi by remember { mutableStateOf(defaultToHindi) }

    LaunchedEffect(quizId) {
        viewModel.loadSolution(quizId, userId)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Result & Solution") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // 2. Add the Language Toggle Button to the Top Bar
                actions = {
                    TextButton(onClick = { isHindi = !isHindi }) {
                        Text(
                            text = if (isHindi) "HI" else "EN",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            )

            when (val state = uiState) {
                is SolutionUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is SolutionUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = Color.Red) }
                is SolutionUiState.Success -> {
                    val data = state.data

                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        // Score Header
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Your Score", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "${data.scoreData?.score ?: 0} / ${data.scoreData?.totalScore ?: 0}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Detailed Questions
                        itemsIndexed(data.detailedResults) { index, result ->
                            val qText = if (isHindi && !result.questionHi.isNullOrBlank()) result.questionHi else result.questionEn

                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Q${index + 1}. $qText", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    result.options.forEach { opt ->
                                        val optText = if (isHindi && !opt.textHi.isNullOrBlank()) opt.textHi else opt.textEn
                                        val bgColor = when {
                                            opt.isCorrect -> Color(0xFFE8F5E9) // Light Green
                                            opt.isSelectedByUser && !opt.isCorrect -> Color(0xFFFFEBEE) // Light Red
                                            else -> MaterialTheme.colorScheme.surface
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(8.dp)).background(bgColor).padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (opt.isCorrect) Icon(Icons.Default.CheckCircle, tint = Color(0xFF4CAF50), contentDescription = null)
                                            else if (opt.isSelectedByUser) Icon(Icons.Default.Cancel, tint = Color(0xFFF44336), contentDescription = null)
                                            else Spacer(modifier = Modifier.width(24.dp))

                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(optText, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }

                                    // Explanation Box
                                    val explanation = if (isHindi && !result.explanationHi.isNullOrBlank()) result.explanationHi else result.explanationEn
                                    if (!explanation.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text("Explanation:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                Text(explanation, style = MaterialTheme.typography.bodyMedium)
                                                if (!result.explanationImage.isNullOrEmpty()) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    AsyncImage(
                                                        model = "https://apnaacoaching.in/config/image/level/${result.explanationImage}",
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp)
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
            }
        }
    }
}
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.Cancel
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import coil.compose.AsyncImage
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StudyQuizSolutionScreen(
//    quizId: String,
//    userId: String,
//    viewModel: StudyQuizSolutionViewModel,
//    navController: NavController,
//    isHindiPreferred: Boolean = false
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    LaunchedEffect(quizId) {
//        viewModel.loadSolution(quizId, userId)
//    }
//
//    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            TopAppBar(
//                title = { Text("Result & Solution") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//
//            when (val state = uiState) {
//                is SolutionUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
//                is SolutionUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = Color.Red) }
//                is SolutionUiState.Success -> {
//                    val data = state.data
//
//                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
//                        // Score Header
//                        item {
//                            Card(
//                                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
//                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
//                            ) {
//                                Column(
//                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    Text("Your Score", style = MaterialTheme.typography.titleMedium)
//                                    Text(
//                                        "${data.scoreData?.score ?: 0} / ${data.scoreData?.totalScore ?: 0}",
//                                        fontSize = 32.sp,
//                                        fontWeight = FontWeight.Bold,
//                                        color = MaterialTheme.colorScheme.primary
//                                    )
//                                }
//                            }
//                        }
//
//                        // Detailed Questions
//                        itemsIndexed(data.detailedResults) { index, result ->
//                            val qText = if (isHindiPreferred && !result.questionHi.isNullOrBlank()) result.questionHi else result.questionEn
//
//                            Card(
//                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
//                                elevation = CardDefaults.cardElevation(2.dp)
//                            ) {
//                                Column(modifier = Modifier.padding(16.dp)) {
//                                    Text("Q${index + 1}. $qText", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
//                                    Spacer(modifier = Modifier.height(16.dp))
//
//                                    result.options.forEach { opt ->
//                                        val optText = if (isHindiPreferred && !opt.textHi.isNullOrBlank()) opt.textHi else opt.textEn
//                                        val bgColor = when {
//                                            opt.isCorrect -> Color(0xFFE8F5E9) // Light Green
//                                            opt.isSelectedByUser && !opt.isCorrect -> Color(0xFFFFEBEE) // Light Red
//                                            else -> MaterialTheme.colorScheme.surface
//                                        }
//
//                                        Row(
//                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(8.dp)).background(bgColor).padding(12.dp),
//                                            verticalAlignment = Alignment.CenterVertically
//                                        ) {
//                                            if (opt.isCorrect) Icon(Icons.Default.CheckCircle, tint = Color(0xFF4CAF50), contentDescription = null)
//                                            else if (opt.isSelectedByUser) Icon(Icons.Default.Cancel, tint = Color(0xFFF44336), contentDescription = null)
//                                            else Spacer(modifier = Modifier.width(24.dp))
//
//                                            Spacer(modifier = Modifier.width(8.dp))
//                                            Text(optText, color = MaterialTheme.colorScheme.onSurface)
//                                        }
//                                    }
//
//                                    // Explanation Box
//                                    val explanation = if (isHindiPreferred && !result.explanationHi.isNullOrBlank()) result.explanationHi else result.explanationEn
//                                    if (!explanation.isNullOrEmpty()) {
//                                        Spacer(modifier = Modifier.height(16.dp))
//                                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
//                                            Column(modifier = Modifier.padding(12.dp)) {
//                                                Text("Explanation:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
//                                                Text(explanation, style = MaterialTheme.typography.bodyMedium)
//                                                if (!result.explanationImage.isNullOrEmpty()) {
//                                                    Spacer(modifier = Modifier.height(8.dp))
//                                                    AsyncImage(
//                                                        model = "https://apnaacoaching.in/config/image/level/${result.explanationImage}",
//                                                        contentDescription = null,
//                                                        modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp)
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}