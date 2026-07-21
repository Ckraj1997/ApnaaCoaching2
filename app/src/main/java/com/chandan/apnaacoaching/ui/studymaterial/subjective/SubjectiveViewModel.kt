package com.chandan.apnaacoaching.ui.studymaterial.subjective

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.SubjectiveQuestion
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SubjectiveUiState {
    object Loading : SubjectiveUiState()
    data class Success(val questions: List<SubjectiveQuestion>) : SubjectiveUiState()
    data class Error(val message: String) : SubjectiveUiState()
}

class SubjectiveViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SubjectiveUiState>(SubjectiveUiState.Loading)
    val uiState: StateFlow<SubjectiveUiState> = _uiState.asStateFlow()

    // --- NEW: Language State ---
    private val _isHindi = MutableStateFlow(true)
    val isHindi = _isHindi.asStateFlow()

    private val _revealedAnswers = MutableStateFlow<Set<String>>(emptySet())
    val revealedAnswers = _revealedAnswers.asStateFlow()

    fun fetchQuestions(groupId: String, levelId: String, catId: String) {
        _uiState.value = SubjectiveUiState.Loading
        _revealedAnswers.value = emptySet()

        viewModelScope.launch {
            try {
                val response =
                    RetrofitClient.StudyApi.getSubjectiveQuestions(groupId, levelId, catId)
                if (response.status == "success") {
                    _uiState.value = SubjectiveUiState.Success(response.questions)
                } else {
                    _uiState.value = SubjectiveUiState.Error("Failed to load questions.")
                }
            } catch (e: Exception) {
                _uiState.value = SubjectiveUiState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    // --- NEW: Toggle Language ---
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