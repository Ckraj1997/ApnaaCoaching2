package com.chandan.apnaacoaching.ui.studymaterial.subjective

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chandan.apnaacoaching.R
import com.chandan.apnaacoaching.data.SubjectiveQuestion

@Composable
fun SubjectiveScreen(
    groupId: String,
    levelId: String,
    catId: String,
    viewModel: SubjectiveViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val revealedAnswers by viewModel.revealedAnswers.collectAsState()

    val questionBaseUrl = "https://apnaacoaching.in/config/image/subjective/"
    val answerBaseUrl = "https://apnaacoaching.in/config/image/subjective/"

    LaunchedEffect(groupId, levelId, catId) {
        viewModel.fetchQuestions(groupId, levelId, catId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- CUSTOM TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.long_questions),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                // --- NEW: Language Toggle Button ---
                TextButton(onClick = { viewModel.toggleLanguage() }) {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = "Lang",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHindi) "ENG" else "हिं", fontWeight = FontWeight.Bold)
                }
            }

            // --- MAIN LIST AREA ---
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                when (val state = uiState) {
                    is SubjectiveUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is SubjectiveUiState.Error -> {
                        Text(
                            state.message,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is SubjectiveUiState.Success -> {
                        if (state.questions.isEmpty()) {
                            Text(
                                stringResource(R.string.no_questions_founds),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.questions) { question ->
                                    val isRevealed = revealedAnswers.contains(question.sub_id)
                                    SubjectiveCard(
                                        question = question,
                                        isHindi = isHindi,
                                        isRevealed = isRevealed,
                                        questionBaseUrl = questionBaseUrl,
                                        answerBaseUrl = answerBaseUrl,
                                        onToggleReveal = { viewModel.toggleAnswerReveal(question.sub_id) }
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
fun SubjectiveCard(
    question: SubjectiveQuestion,
    isHindi: Boolean,
    isRevealed: Boolean,
    questionBaseUrl: String,
    answerBaseUrl: String,
    onToggleReveal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {

            // --- 1. QUESTION TEXT ---
            // Fallback to English text if Hindi text is null/empty
            val qText =
                if (isHindi && !question.question_hi.isNullOrEmpty()) question.question_hi else question.question
            Text(
                text = stringResource(R.string.qs, qText ?: ""),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 24.sp
            )

            // --- 2. QUESTION IMAGE ---
            // Fallback to English image if Hindi image is null/empty
            val rawQImg =
                if (isHindi && !question.question_img_hi.isNullOrEmpty()) question.question_img_hi else question.question_img

            if (!rawQImg.isNullOrEmpty() && rawQImg != "image.hindi" && rawQImg != "question.image") {
                // FIX: Replace physical spaces with URL-friendly %20
                val safeQImg = rawQImg.replace(" ", "%20")

                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = questionBaseUrl + safeQImg,
                    contentDescription = "Question Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 3. SHOW/HIDE ANSWER BUTTON ---
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleReveal() }
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRevealed) stringResource(R.string.hide_answers) else stringResource(
                        R.string.view_full_answers
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // --- 4. REVEALED LONG ANSWER ---
            if (isRevealed) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                // Answer Text (with fallback)
                val aText =
                    if (isHindi && !question.answer_hi.isNullOrEmpty()) question.answer_hi else question.answer
                Text(
                    text = aText ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                // Answer Image (with fallback)
                val rawAImg =
                    if (isHindi && !question.answer_img_hi.isNullOrEmpty()) question.answer_img_hi else question.answer_img

                if (!rawAImg.isNullOrEmpty() && rawAImg != "image.hindi" && rawAImg != "answer.image") {
                    // FIX: Replace physical spaces with URL-friendly %20
                    val safeAImg = rawAImg.replace(" ", "%20")

                    Spacer(modifier = Modifier.height(12.dp))
                    AsyncImage(
                        model = answerBaseUrl + safeAImg,
                        contentDescription = stringResource(R.string.answer_image),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}