package com.chandan.apnaacoaching.ui.kbc

import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chandan.apnaacoaching.R

// KBC Color Palette
val KbcDarkBlue = Color(0xFF020228)
val KbcLightBlue = Color(0xFF003366)
val KbcGold = Color(0xFFFFD700)
val KbcTextWhite = Color(0xFFE0E0E0)
val KbcCorrectGreen = Color(0xFF4CAF50)
val KbcWrongRed = Color(0xFFF44336)
val KbcLockedOrange = Color(0xFFFF9800)

@Composable
fun KbcScreen(
    viewModel: KbcViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var bgmPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var sfxPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    fun playSfx(resId: Int) {
        try {
            sfxPlayer?.release()
            sfxPlayer = MediaPlayer.create(context, resId)
            sfxPlayer?.start()
        } catch (e: Exception) { /* File missing */ }
    }

    fun playBgm(resId: Int) {
        try {
            if (bgmPlayer != null) bgmPlayer?.release()
            bgmPlayer = MediaPlayer.create(context, resId)
            bgmPlayer?.isLooping = true
            bgmPlayer?.start()
        } catch (e: Exception) { /* File missing */ }
    }

    DisposableEffect(Unit) {
        onDispose {
            bgmPlayer?.release()
            sfxPlayer?.release()
        }
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is KbcGameState.Instructions -> playBgm(R.raw.kbc_intro)

            is KbcGameState.Playing -> {
                if (state.lockedOptionId == null) {
                    playBgm(R.raw.kbc_bgm) // Normal question suspense
                } else if (state.lockedOptionId != null && state.revealedCorrectOptionId == null) {
                    bgmPlayer?.pause()
                    playSfx(R.raw.kbc_lock) // "Computer ji lock kiya jaye"
                } else if (state.revealedCorrectOptionId != null) {
                    val isCorrect = state.lockedOptionId == state.revealedCorrectOptionId
                    playSfx(if (isCorrect) R.raw.kbc_correct else R.raw.kbc_wrong)
                }
            }

            is KbcGameState.GameOver -> {
                bgmPlayer?.release()
                if (state.message.contains("Time")) playSfx(R.raw.kbc_wrong)
            }
            else -> {}
        }
    }
    // Start the game when the screen opens
    LaunchedEffect(Unit) { viewModel.initGame(context) }

    // Main Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(KbcLightBlue, KbcDarkBlue)))
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is KbcGameState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = KbcGold)
            is KbcGameState.Instructions -> InstructionsScreen(state.highScore, onStart = { viewModel.startNewGame() })
            is KbcGameState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message, color = Color.Red, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.startNewGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = KbcGold)
                    ) {
                        Text("Retry", color = KbcDarkBlue)
                    }
                }
            }

            is KbcGameState.GameOver -> GameOverScreen(state, onExit = { navController.popBackStack() })

            is KbcGameState.Playing -> PlayingScreen(state, viewModel, onQuit = { viewModel.quitGame(context) })        }
    }
}

@Composable
fun InstructionsScreen(highScore: String, onStart: () -> Unit) {
    var isHindi by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.EmojiEvents, contentDescription = "Trophy", tint = KbcGold, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Highest Score: $highScore", color = KbcGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        // Language Toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("EN", color = if (!isHindi) KbcGold else Color.Gray, fontWeight = FontWeight.Bold)
            Switch(
                checked = isHindi,
                onCheckedChange = { isHindi = it },
                colors = SwitchDefaults.colors(checkedThumbColor = KbcGold, checkedTrackColor = KbcLightBlue)
            )
            Text("HI", color = if (isHindi) KbcGold else Color.Gray, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = KbcDarkBlue.copy(alpha = 0.7f)),
            border = BorderStroke(1.dp, KbcGold)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (isHindi) "खेल के नियम:" else "Game Rules:",
                    color = KbcGold, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                val rules = if (isHindi) {
                    "1. कुल 15 प्रश्न हैं।\n2. प्रश्न 1-5 के लिए 45 सेकंड और 6-10 के लिए 60 सेकंड का समय है।\n3. ₹10,000 और ₹3,20,000 सुरक्षित पड़ाव हैं।\n4. आप किसी भी समय गेम छोड़ सकते हैं।"
                } else {
                    "1. There are 15 questions in total.\n2. Q1-5 have 45s, Q6-10 have 60s.\n3. ₹10,000 and ₹3,20,000 are safe havens.\n4. You can quit anytime to take current winnings."
                }
                Text(text = rules, color = Color.White, fontSize = 16.sp, lineHeight = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(containerColor = KbcGold),
            modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
        ) {
            Text(if (isHindi) "खेल शुरू करें" else "START GAME", color = KbcDarkBlue, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        }
    }
}
@Composable
fun PlayingScreen(state: KbcGameState.Playing, viewModel: KbcViewModel, onQuit: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // --- TOP BAR ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onQuit, enabled = state.lockedOptionId == null) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Quit", tint = if (state.lockedOptionId == null) Color.Red else Color.Gray)
            }
            Card(colors = CardDefaults.cardColors(containerColor = KbcGold.copy(alpha = 0.2f)), border = BorderStroke(1.dp, KbcGold), shape = RoundedCornerShape(20.dp)) {
                Text(text = "Prize: ${state.currentPrize}", color = KbcGold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            Text(text = "${state.currentLevel}/15", color = KbcTextWhite, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LifelineButton(icon = Icons.Default.StarHalf, label = "50:50", isUsed = state.is5050Used, onClick = { viewModel.use5050() })
            LifelineButton(icon = Icons.Default.BarChart, label = "Poll", isUsed = state.isPollUsed, onClick = { viewModel.useAudiencePoll() })
        }

        Spacer(modifier = Modifier.height(24.dp))
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { if (state.timerSeconds == 999) 1f else state.timerSeconds / (if (state.currentLevel <= 5) 45f else 60f) },
                modifier = Modifier.size(70.dp), color = KbcGold, trackColor = KbcGold.copy(alpha = 0.2f), strokeWidth = 6.dp
            )
            Text(text = if (state.timerSeconds == 999) "∞" else state.timerSeconds.toString(), color = KbcTextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- QUESTION CARD ---
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = KbcLightBlue),
            border = BorderStroke(2.dp, KbcGold),
            shape = RoundedCornerShape(16.dp)
        ) {
            val questionText = state.currentQuestion.questionHi?.takeIf { it.isNotBlank() } ?: state.currentQuestion.questionEn
            Text(
                text = questionText,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- DYNAMIC OPTIONS ---
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 8.dp)) {
            val prefixes = listOf("A", "B", "C", "D")
            state.currentQuestion.options.forEachIndexed { index, option ->
                val optText = option.textHi?.takeIf { it.isNotBlank() } ?: option.textEn

                // Determine Option Color State
                val cardColor = when {
                    state.revealedCorrectOptionId == option.optionId -> KbcCorrectGreen
                    state.revealedCorrectOptionId != null && state.lockedOptionId == option.optionId -> KbcWrongRed
                    state.lockedOptionId == option.optionId -> KbcLockedOrange
                    else -> KbcDarkBlue
                }

                KbcOptionRow(
                    prefix = prefixes.getOrElse(index) { "" },
                    text = optText,
                    isHidden = option.isHidden,
                    pollPercentage = state.pollResults?.get(option.optionId),
                    backgroundColor = cardColor,
                    onClick = {
                        if (state.lockedOptionId == null) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.submitAnswer(option.optionId, context)
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun KbcOptionRow(prefix: String, text: String, isHidden: Boolean, pollPercentage: Int?, backgroundColor: Color, onClick: () -> Unit) {
    if (isHidden) {
        Box(modifier = Modifier.fillMaxWidth().height(60.dp))
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(60.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, KbcTextWhite.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(30.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (pollPercentage != null) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(pollPercentage / 100f).background(KbcLightBlue.copy(alpha = 0.8f)))
            }
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$prefix: ", color = if (backgroundColor == KbcDarkBlue) KbcGold else Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = text, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
                if (pollPercentage != null) Text(text = "$pollPercentage%", color = KbcGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}
@Composable
fun LifelineButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isUsed: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick, enabled = !isUsed,
            modifier = Modifier.size(56.dp).clip(CircleShape).background(if (isUsed) Color.Gray else KbcLightBlue).padding(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = if (isUsed) Color.DarkGray else KbcGold, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = if (isUsed) Color.Gray else KbcTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GameOverScreen(state: KbcGameState.GameOver, onExit: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = state.message, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        if (state.isNewHighScore) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("🏆 NEW HIGH SCORE! 🏆", color = KbcCorrectGreen, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Total Winnings", color = KbcTextWhite, fontSize = 18.sp)
        Text(text = state.wonAmount, color = KbcGold, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onExit, colors = ButtonDefaults.buttonColors(containerColor = KbcGold)) {
            Text("Back to Menu", color = KbcDarkBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
        }
    }
}