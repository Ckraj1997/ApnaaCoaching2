package com.chandan.apnaacoaching.ui.studymaterial.pdf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.PdfItem
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PdfUiState {
    object Loading : PdfUiState()
    data class Success(val pdfs: List<PdfItem>) : PdfUiState()
    data class Error(val message: String) : PdfUiState()
}

class PdfViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PdfUiState>(PdfUiState.Loading)
    val uiState: StateFlow<PdfUiState> = _uiState.asStateFlow()

    fun fetchPdfs(groupId: String, levelId: String, catId: String) {
        _uiState.value = PdfUiState.Loading

        viewModelScope.launch {
            try {
                val response = RetrofitClient.StudyApi.getPdfs(groupId, levelId, catId)
                if (response.status == "success") {
                    _uiState.value = PdfUiState.Success(response.pdfs)
                } else {
                    _uiState.value = PdfUiState.Error("Failed to load PDFs.")
                }
            } catch (e: Exception) {
                _uiState.value = PdfUiState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}