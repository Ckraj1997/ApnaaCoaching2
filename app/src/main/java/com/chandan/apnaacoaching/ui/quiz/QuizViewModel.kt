package com.chandan.apnaacoaching.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.CbtQuestion
import com.chandan.apnaacoaching.data.QuizSubmission
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class QuizViewModel : ViewModel() {
    private val _questions = MutableStateFlow<List<CbtQuestion>>(emptyList())
    val questions = _questions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _userAnswers = MutableStateFlow<MutableMap<String, String>>(mutableMapOf())
    val userAnswers = _userAnswers.asStateFlow()

    private val _formattedTime = MutableStateFlow("00:00:00")
    val formattedTime = _formattedTime.asStateFlow()
    private var timerJob: Job? = null

    private val _markedQuestions = MutableStateFlow<Set<String>>(emptySet())
    val markedQuestions = _markedQuestions.asStateFlow()

    private val _isAutoSubmitted = MutableStateFlow(false)
    val isAutoSubmitted = _isAutoSubmitted.asStateFlow()

    private val _visitedQuestions = MutableStateFlow<Set<String>>(emptySet())
    val visitedQuestions = _visitedQuestions.asStateFlow()

    fun markQuestionAsVisited(questionId: String) {

        _visitedQuestions.update { currentSet ->
            currentSet + questionId
        }
    }

    fun toggleMarkForReview() {
        val currentQuestionId = _questions.value.getOrNull(_currentIndex.value)?.id ?: return
        val currentMarked = _markedQuestions.value.toMutableSet()

        if (currentMarked.contains(currentQuestionId)) {
            currentMarked.remove(currentQuestionId)
        } else {
            currentMarked.add(currentQuestionId)
        }
        _markedQuestions.value = currentMarked
    }

    fun clearAnswer() {
        val currentQuestionId = _questions.value.getOrNull(_currentIndex.value)?.id ?: return
        val currentAnswers = _userAnswers.value.toMutableMap()

        currentAnswers.remove(currentQuestionId)
        _userAnswers.value = currentAnswers
    }

    fun startTimer(durationInMinutes: Int, userId: String, quizId: Int) {
        timerJob?.cancel() // Reset if already running
        var timeLeftInSeconds = durationInMinutes * 60

        timerJob = viewModelScope.launch {
            while (timeLeftInSeconds > 0) {
                val h = timeLeftInSeconds / 3600
                val m = (timeLeftInSeconds % 3600) / 60
                val s = timeLeftInSeconds % 60

                _formattedTime.value = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)

                delay(1000L.milliseconds) // Wait 1 second
                timeLeftInSeconds--
            }
            _formattedTime.value = "00:00:00"

            submitQuiz(userId, quizId) { isSuccess, message ->
                if (isSuccess) {

                    _isAutoSubmitted.value = true
                }
            }
        }
    }

    fun goToQuestion(index: Int) {
        if (index in _questions.value.indices) {
            _currentIndex.value = index
        }
    }

    fun fetchQuestions(quizId: Int, userId: String, durationInMinutes: Int) {
        viewModelScope.launch {
            try {

                val responseList = RetrofitClient.practiceApi.getQuestions(quizId)

                _questions.value = responseList
                _currentIndex.value = 0
                _userAnswers.value = mutableMapOf()
                _markedQuestions.value = emptySet()
                _visitedQuestions.value = emptySet()
                _isAutoSubmitted.value = false

                startTimer(durationInMinutes, userId, quizId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectAnswer(questionId: String, optionId: String) { // <-- String पैरामीटर्स
        val currentMap = _userAnswers.value.toMutableMap()
        currentMap[questionId] = optionId
        _userAnswers.value = currentMap
    }

    fun nextQuestion() {
        if (_currentIndex.value < _questions.value.size - 1) {
            _currentIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
        }
    }

    fun getSubmissionData(userId: String, quizId: Int): QuizSubmission {
        val allQuestions = _questions.value

        val answerList = mutableListOf<Int?>()
        val statusList = mutableListOf<String>()
        val questionNumberList = mutableListOf<Int>()

        allQuestions.forEach { question ->
            val selectedOptionId = _userAnswers.value[question.id]

            questionNumberList.add(question.id.toInt())

            answerList.add(selectedOptionId?.toInt())

            val status = if (selectedOptionId != null) "answered" else "not_answered"
            statusList.add(status)
        }

        return QuizSubmission(
            user_id = userId,
            quiz_id = quizId,
            answers = answerList,
            statuses = statusList,
            question_number = questionNumberList
        )
    }

    fun submitQuiz(userId: String, quizId: Int, onComplete: (Boolean, String) -> Unit) {
        val submission = getSubmissionData(userId, quizId)

        viewModelScope.launch {
            try {
                val response = RetrofitClient.practiceApi.submitAnswers(submission)
                if (response.isSuccessful) {
                    onComplete(true, "Submitted successfully!")
                } else {
                    onComplete(false, "Submission failed: ${response.code()}")
                }
            } catch (e: Exception) {
                onComplete(false, "Network error: ${e.message}")
            }
        }
    }

    fun calculateResult(): Triple<Int, Int, Int> {
        var correct = 0
        var wrong = 0
        val unattempted = _questions.value.size - _userAnswers.value.size

        _questions.value.forEach { question ->
            val selectedOptionId = _userAnswers.value[question.id]

            if (selectedOptionId != null) {
                if (selectedOptionId == question.right_id) {
                    correct++
                } else {
                    wrong++
                }
            }
        }

        return Triple(correct, wrong, unattempted)
    }

    fun calculateFinalScore(plusPoint: Int, minusPoint: Int): Int {
        var correct = 0
        var wrong = 0

        _questions.value.forEach { question ->
            val selectedOptionId = _userAnswers.value[question.id]
            if (selectedOptionId != null) {
                if (selectedOptionId == question.right_id) {
                    correct++
                } else {
                    wrong++
                }
            }
        }

        return (correct * plusPoint) - (wrong * minusPoint)
    }
}