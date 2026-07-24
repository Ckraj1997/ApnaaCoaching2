package com.chandan.apnaacoaching.ui.kbc

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.KbcQuestion
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

sealed class KbcGameState {
    object Loading : KbcGameState()
    data class Instructions(val highScore: String) : KbcGameState()
    data class Playing(
        val currentQuestion: KbcQuestion,
        val currentLevel: Int,
        val timerSeconds: Int,
        val currentPrize: String,
        val is5050Used: Boolean,
        val isPollUsed: Boolean,
        val pollResults: Map<String, Int>? = null,
        val lockedOptionId: String? = null,           // Suspense Phase 1: Lock the answer
        val revealedCorrectOptionId: String? = null   // Suspense Phase 2: Reveal the true answer
    ) : KbcGameState()
    data class GameOver(val wonAmount: String, val message: String, val isNewHighScore: Boolean) : KbcGameState()
    data class Error(val message: String) : KbcGameState()
}

class KbcViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<KbcGameState>(KbcGameState.Loading)
    val uiState: StateFlow<KbcGameState> = _uiState.asStateFlow()

    private var questionList: List<KbcQuestion> = emptyList()
    private var currentLevelIndex = 0
    private var timerJob: Job? = null
    private var currentHighScoreIndex = 0

    // KBC Prize Ladder - CRITICAL FIX: Ensure index 0 is "₹0"
    private val prizeLadder = listOf(
        "₹0", "₹1,000", "₹2,000", "₹3,000", "₹5,000", "₹10,000", // Index 5 is Safe Haven 1
        "₹20,000", "₹40,000", "₹80,000", "₹1,60,000", "₹3,20,000", // Index 10 is Safe Haven 2
        "₹6,40,000", "₹12,50,000", "₹25,00,000", "₹50,00,000", "₹1 Crore"
    )

    fun initGame(context: Context) {
        val prefs = context.getSharedPreferences("KbcPrefs", Context.MODE_PRIVATE)
        currentHighScoreIndex = prefs.getInt("high_score_index", 0)
        _uiState.value = KbcGameState.Instructions(prizeLadder[currentHighScoreIndex])
    }

    fun startNewGame() {
        _uiState.value = KbcGameState.Loading
        currentLevelIndex = 1 // Start at ₹1,000 (Index 1)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.practiceApi.startKbcSession()
                if (response.status == "success" && response.kbcQuestions.size == 15) {
                    questionList = response.kbcQuestions
                    loadQuestion()
                } else {
                    _uiState.value = KbcGameState.Error("Could not load KBC questions.")
                }
            } catch (e: Exception) {
                _uiState.value = KbcGameState.Error(e.localizedMessage ?: "Network Error")
            }
        }
    }
    private fun loadQuestion() {
        timerJob?.cancel()
        val question = questionList[currentLevelIndex - 1]

        val initialTime = when (currentLevelIndex) {
            in 1..5 -> 45
            in 6..10 -> 60
            else -> 999
        }

        val currentState = _uiState.value
        val used5050 = if (currentState is KbcGameState.Playing) currentState.is5050Used else false
        val usedPoll = if (currentState is KbcGameState.Playing) currentState.isPollUsed else false

        _uiState.value = KbcGameState.Playing(
            currentQuestion = question,
            currentLevel = currentLevelIndex,
            timerSeconds = initialTime,
            currentPrize = prizeLadder[currentLevelIndex],
            is5050Used = used5050,
            isPollUsed = usedPoll,
            lockedOptionId = null,
            revealedCorrectOptionId = null
        )

        if (initialTime != 999) startTimer(initialTime)
    }

    private fun startTimer(seconds: Int) {
        timerJob = viewModelScope.launch {
            for (i in seconds downTo 0) {
                val state = _uiState.value
                if (state is KbcGameState.Playing && state.lockedOptionId == null) {
                    _uiState.value = state.copy(timerSeconds = i)
                    if (i == 0) triggerGameOver(isTimeOut = true, context = null)
                }
                delay(1000)
            }
        }
    }

    fun submitAnswer(optionId: String, context: Context) {
        val state = _uiState.value
        // Prevent clicking multiple times or after time is up
        if (state !is KbcGameState.Playing || state.lockedOptionId != null) return

        timerJob?.cancel() // Stop the clock immediately

        // 1. Lock the answer (Turns yellow in UI, plays lock sound)
        _uiState.value = state.copy(lockedOptionId = optionId)

        viewModelScope.launch {
            // Suspense Delay
            delay(3000)

            val isCorrect = state.currentQuestion.options.find { it.optionId == optionId }?.isCorrect == true
            val correctOptionId = state.currentQuestion.options.find { it.isCorrect }?.optionId

            // 2. Reveal the answer (Turns green/red in UI, plays correct/wrong sound)
            _uiState.value = (_uiState.value as KbcGameState.Playing).copy(revealedCorrectOptionId = correctOptionId)

            // Let the user see the result for a moment
            delay(3500)

            // 3. Move forward or Game Over
            if (isCorrect) {
                if (currentLevelIndex == 15) {
                    saveHighScore(15, context)
                    _uiState.value = KbcGameState.GameOver(prizeLadder[15], "Congratulations! You are a Crorepati!", isNewHighScore = true)
                } else {
                    currentLevelIndex++
                    loadQuestion()
                }
            } else {
                triggerGameOver(isTimeOut = false, context = context)
            }
        }
    }
    private fun triggerGameOver(isTimeOut: Boolean, context: Context?) {
        val safeHavenIndex = when {
            currentLevelIndex > 10 -> 10 // Passed Q10, keeps ₹3,20,000
            currentLevelIndex > 5 -> 5   // Passed Q5, keeps ₹10,000
            else -> 0                    // Failed between Q1 and Q5, gets ₹0
        }

        var isNewHigh = false
        if (context != null) {
            isNewHigh = saveHighScore(safeHavenIndex, context)
        }

        val reason = if (isTimeOut) "Time's up!" else "Wrong Answer!"
        _uiState.value = KbcGameState.GameOver(prizeLadder[safeHavenIndex], reason, isNewHigh)
    }
    fun quitGame(context: Context) {
        timerJob?.cancel()
        val takeHomeIndex = currentLevelIndex - 1
        val isNewHigh = saveHighScore(takeHomeIndex, context)
        _uiState.value = KbcGameState.GameOver(prizeLadder[takeHomeIndex], "You chose to quit the game.", isNewHigh)
    }

    private fun saveHighScore(achievedIndex: Int, context: Context): Boolean {
        if (achievedIndex > currentHighScoreIndex) {
            currentHighScoreIndex = achievedIndex
            val prefs = context.getSharedPreferences("KbcPrefs", Context.MODE_PRIVATE)
            prefs.edit().putInt("high_score_index", achievedIndex).apply()
            return true
        }
        return false
    }

    // --- LIFELINES ---
    fun use5050() {
        val state = _uiState.value
        if (state is KbcGameState.Playing && !state.is5050Used) {
            val options = state.currentQuestion.options
            val wrongOptions = options.filter { !it.isCorrect }.shuffled().take(2)

            wrongOptions.forEach { it.isHidden = true }

            _uiState.value = state.copy(is5050Used = true)
        }
    }

    fun useAudiencePoll() {
        val state = _uiState.value
        if (state is KbcGameState.Playing && !state.isPollUsed) {
            val options = state.currentQuestion.options

            // Generate fake realistic percentages
            var remainingPercent = 100
            val correctPercent = Random.nextInt(55, 80)
            remainingPercent -= correctPercent

            val pollData = mutableMapOf<String, Int>()

            options.forEach { opt ->
                if (opt.isCorrect) {
                    pollData[opt.optionId] = correctPercent
                } else {
                    val percent = if (opt == options.last { !it.isCorrect }) remainingPercent else Random.nextInt(0, remainingPercent)
                    pollData[opt.optionId] = percent
                    remainingPercent -= percent
                }
            }

            _uiState.value = state.copy(isPollUsed = true, pollResults = pollData)
        }
    }
}