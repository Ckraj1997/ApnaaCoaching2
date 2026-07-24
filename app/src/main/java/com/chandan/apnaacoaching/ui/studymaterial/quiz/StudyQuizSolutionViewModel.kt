package com.chandan.apnaacoaching.ui.studymaterial.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.StudyQuizSolutionResponse
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SolutionUiState {
    object Loading : SolutionUiState()
    data class Success(val data: StudyQuizSolutionResponse) : SolutionUiState()
    data class Error(val message: String) : SolutionUiState()
}

class StudyQuizSolutionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SolutionUiState>(SolutionUiState.Loading)
    val uiState: StateFlow<SolutionUiState> = _uiState.asStateFlow()

    fun loadSolution(quizId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.StudyApi.getStudyQuizSolution(quizId, userId)
                if (response.status == "success") {
                    _uiState.value = SolutionUiState.Success(response)
                } else {
                    _uiState.value = SolutionUiState.Error(response.message ?: "Failed to load solution")
                }
            } catch (e: Exception) {
                _uiState.value = SolutionUiState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}