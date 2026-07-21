package com.chandan.apnaacoaching.ui.studymaterial.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.QuizItem
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class QuizListUiState {
    object Loading : QuizListUiState()
    data class Success(val quizzes: List<QuizItem>) : QuizListUiState()
    data class Error(val message: String) : QuizListUiState()
}

class QuizListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<QuizListUiState>(QuizListUiState.Loading)
    val uiState: StateFlow<QuizListUiState> = _uiState.asStateFlow()

    fun fetchQuizList(userId: String, groupId: String, levelId: String, catId: String) {
        _uiState.value = QuizListUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.StudyApi.getQuizList(userId, catId, levelId, groupId)
                if (response.status == "success") {
                    _uiState.value = QuizListUiState.Success(response.quizzes)
                } else {
                    _uiState.value =
                        QuizListUiState.Error(response.message ?: "Failed to load quizzes.")
                }
            } catch (e: Exception) {
                _uiState.value = QuizListUiState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}