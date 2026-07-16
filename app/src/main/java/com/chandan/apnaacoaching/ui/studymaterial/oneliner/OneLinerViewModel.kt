package com.chandan.apnaacoaching.ui.studymaterial.oneliner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.OneLinerQuestion
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OneLinerUiState {
    object Loading : OneLinerUiState()
    data class Success(val questions: List<OneLinerQuestion>) : OneLinerUiState()
    data class Error(val message: String) : OneLinerUiState()
}

class OneLinerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<OneLinerUiState>(OneLinerUiState.Loading)
    val uiState: StateFlow<OneLinerUiState> = _uiState.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage = _currentPage.asStateFlow()

    private val _isHindi = MutableStateFlow(true)
    val isHindi = _isHindi.asStateFlow()

    // Keeps track of which question IDs have their answers revealed
    private val _revealedAnswers = MutableStateFlow<Set<String>>(emptySet())
    val revealedAnswers = _revealedAnswers.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages = _totalPages.asStateFlow()

    val limit = 15 // Questions per page

    fun fetchOneLiners(groupId: String, levelId: String, catId: String, page: Int) {
        _uiState.value = OneLinerUiState.Loading
        _currentPage.value = page
        _revealedAnswers.value = emptySet() // Reset reveals on page change

        val offset = (page - 1) * limit

        viewModelScope.launch {
            try {
                val response =
                    RetrofitClient.StudyApi.getOneLiners(groupId, levelId, catId, offset, limit)
                if (response.status == "success") {
                    // --- NEW: Calculate Total Pages ---
                    val totalQ = response.total_questions
                    // Divide total questions by limit, round up (e.g., 16/15 = 2 pages), and ensure at least 1 page
                    _totalPages.value =
                        Math.ceil(totalQ.toDouble() / limit).toInt().coerceAtLeast(1)

                    _uiState.value = OneLinerUiState.Success(response.data)
                } else {
                    _uiState.value = OneLinerUiState.Error(response.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _uiState.value = OneLinerUiState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun toggleLanguage() {
        _isHindi.value = !_isHindi.value
    }

    fun toggleAnswerReveal(questionId: String) {
        val current = _revealedAnswers.value.toMutableSet()
        if (current.contains(questionId)) {
            current.remove(questionId)
        } else {
            current.add(questionId)
        }
        _revealedAnswers.value = current
    }
}