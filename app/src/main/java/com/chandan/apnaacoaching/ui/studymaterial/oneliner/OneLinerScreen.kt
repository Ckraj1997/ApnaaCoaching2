package com.chandan.apnaacoaching.ui.studymaterial.oneliner

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.chandan.apnaacoaching.data.OneLinerQuestion

@Composable
fun OneLinerScreen(
    groupId: String,
    levelId: String,
    catId: String,
    viewModel: OneLinerViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val revealedAnswers by viewModel.revealedAnswers.collectAsState()

    val baseUrl = "https://apnaacoaching.in"
    val questionBaseUrl = "https://apnaacoaching.in/config/image/question/"
    val optionBaseUrl = "https://apnaacoaching.in/config/image/option/"

    val totalPages by viewModel.totalPages.collectAsState()

    LaunchedEffect(groupId, levelId, catId, currentPage) {
        viewModel.fetchOneLiners(groupId, levelId, catId, currentPage)
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
                    text = stringResource(R.string.one_liners),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                // Language Toggle Button
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (val state = uiState) {
                    is OneLinerUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is OneLinerUiState.Error -> {
                        Text(
                            state.message,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is OneLinerUiState.Success -> {
                        if (state.questions.isEmpty()) {
                            Text(
                                stringResource(R.string.no_questions_found),
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
                                    val isRevealed = revealedAnswers.contains(question.question_id)
                                    OneLinerCard(
                                        question = question,
                                        isHindi = isHindi,
                                        isRevealed = isRevealed,
                                        questionBaseUrl = questionBaseUrl, // Passed here
                                        optionBaseUrl = optionBaseUrl,
                                        onToggleReveal = { viewModel.toggleAnswerReveal(question.question_id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- BOTTOM PAGINATION PALETTE ---
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    items(totalPages) { index ->
                        val pageNum = index + 1
                        val isCurrent = pageNum == currentPage

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCurrent) MaterialTheme.colorScheme.primary else Color(
                                        0xFFEEEEEE
                                    )
                                )
                                .clickable {
                                    viewModel.fetchOneLiners(
                                        groupId,
                                        levelId,
                                        catId,
                                        pageNum
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = pageNum.toString(),
                                color = if (isCurrent) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OneLinerCard(
    question: OneLinerQuestion,
    isHindi: Boolean,
    isRevealed: Boolean,
    questionBaseUrl: String,
    optionBaseUrl: String,
    onToggleReveal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            // Question Text
            val qText = if (isHindi) question.question_name_Hi else question.question
            Text(
                text = stringResource(R.string.q, qText ?: ""),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Question Image
            // --- 2. QUESTION IMAGE ---
            val qImg = if (isHindi) question.question_image_Hi else question.question_image
            // Checks if it's not null, not empty, and not the placeholder "image.hindi"
            if (!qImg.isNullOrEmpty() && qImg != "image.hindi") {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = questionBaseUrl + qImg,
                    contentDescription = "Question Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show/Hide Answer Button
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
                    text = if (isRevealed) stringResource(R.string.hide_answer) else stringResource(
                        R.string.show_answer
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // The Revealed Answer
            if (isRevealed && question.options.isNotEmpty()) {
                val correctOpt = question.options[0] // Since PHP only returns is_right=1

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                val aText = if (isHindi) correctOpt.option_txt_hi else correctOpt.option_txt
                Text(
                    text = stringResource(R.string.ans, aText ?: ""),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50), // Green for correct answer
                    fontSize = 15.sp
                )

                val aImg = if (isHindi) correctOpt.opt_image_hi else correctOpt.opt_image
                if (!aImg.isNullOrEmpty() && aImg != "image.hindi") {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = optionBaseUrl + aImg,
                        contentDescription = "Answer Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Description / Explanation (if any)
                val descText = if (isHindi) correctOpt.option_desc_hi else correctOpt.option_desc
                if (!descText.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.explanations, descText),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                val descImg =
                    if (isHindi) correctOpt.option_desc_image_hi else correctOpt.option_desc_image
                if (!descImg.isNullOrEmpty() && descImg != "image.hindi") {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = optionBaseUrl + descImg,
                        contentDescription = "Explanation Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }


        }
    }
}
