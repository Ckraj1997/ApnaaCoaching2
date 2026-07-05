package com.chandan.apnaacoaching.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.FullResultResponse
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ResultUiState {
    object Loading : ResultUiState()
    data class Awaiting(val releaseTime: String) : ResultUiState()
    data class Success(val data: FullResultResponse) : ResultUiState()
    data class Error(val message: String) : ResultUiState()
}

class ResultViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ResultUiState>(ResultUiState.Loading)
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    fun fetchDetailedResult(quizId: Int, userId: String) {
        _uiState.value = ResultUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.practiceApi.getFullResult(quizId, userId)

                when (response.status) {
                    "success" -> _uiState.value = ResultUiState.Success(response)
                    "awaiting" -> _uiState.value =
                        ResultUiState.Awaiting(response.release_time ?: "Unknown")

                    else -> _uiState.value =
                        ResultUiState.Error(response.message ?: "Failed to load results")
                }
            } catch (e: Exception) {
                _uiState.value = ResultUiState.Error("Network Error: ${e.localizedMessage}")
            }
        }
    }
}