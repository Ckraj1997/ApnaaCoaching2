package com.chandan.apnaacoaching.ui.studymaterial.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.StudyQuizQuestion
import com.chandan.apnaacoaching.data.SubmitQuizRequest
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class QuizPlayerUiState {
    object Loading : QuizPlayerUiState()
    data class Success(
        val questions: List<StudyQuizQuestion>,
        val currentIndex: Int = 0,
        val selectedAnswers: Map<String, String> = emptyMap(),
        val isSubmitting: Boolean = false,
        val isFinished: Boolean = false
    ) : QuizPlayerUiState()
    data class Error(val message: String) : QuizPlayerUiState()
}

class QuizPlayerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<QuizPlayerUiState>(QuizPlayerUiState.Loading)
    val uiState: StateFlow<QuizPlayerUiState> = _uiState.asStateFlow()

    fun loadStudyMaterialQuiz(quizId: String) {
        _uiState.value = QuizPlayerUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.StudyApi.getStudyQuizQuestions(quizId)
                if (response.status == "success" && response.questions.isNotEmpty()) {
                    _uiState.value = QuizPlayerUiState.Success(questions = response.questions)
                } else {
                    _uiState.value = QuizPlayerUiState.Error(response.message ?: "No questions found.")
                }
            } catch (e: Exception) {
                _uiState.value = QuizPlayerUiState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun selectOption(questionId: String, optionId: String) {
        val currentState = _uiState.value
        if (currentState is QuizPlayerUiState.Success) {
            val updatedAnswers = currentState.selectedAnswers.toMutableMap()
            updatedAnswers[questionId] = optionId
            _uiState.value = currentState.copy(selectedAnswers = updatedAnswers)
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState is QuizPlayerUiState.Success) {
            if (currentState.currentIndex < currentState.questions.size - 1) {
                _uiState.value = currentState.copy(currentIndex = currentState.currentIndex + 1)
            }
        }
    }

    fun previousQuestion() {
        val currentState = _uiState.value
        if (currentState is QuizPlayerUiState.Success) {
            if (currentState.currentIndex > 0) {
                _uiState.value = currentState.copy(currentIndex = currentState.currentIndex - 1)
            }
        }
    }

    fun submitQuiz(quizId: String, userId: String) {
        val currentState = _uiState.value
        if (currentState is QuizPlayerUiState.Success) {
            _uiState.value = currentState.copy(isSubmitting = true)
            viewModelScope.launch {
                try {
                    val request = SubmitQuizRequest(quizId, userId, currentState.selectedAnswers)
                    val response = RetrofitClient.StudyApi.submitStudyQuiz(request)

                    if (response.status == "success") {
                        _uiState.value = currentState.copy(isSubmitting = false, isFinished = true)
                    } else {
                        _uiState.value = QuizPlayerUiState.Error(response.message ?: "Submission failed")
                    }
                } catch (e: Exception) {
                    _uiState.value = currentState.copy(isSubmitting = false)
                }
            }
        }
    }

    fun resetFinishedState() {
        val currentState = _uiState.value
        if (currentState is QuizPlayerUiState.Success) {
            _uiState.value = currentState.copy(isFinished = false)
        }
    }
}