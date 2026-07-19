package com.chandan.apnaacoaching.ui.studymaterial.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.UpdateItem
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UpdateUiState {
    object Loading : UpdateUiState()
    data class Success(val updates: List<UpdateItem>) : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
}

class UpdateViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Loading)
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    fun fetchUpdates(groupId: String, levelId: String, catId: String) {
        _uiState.value = UpdateUiState.Loading

        viewModelScope.launch {
            try {
                val response = RetrofitClient.StudyApi.getUpdates(groupId, levelId, catId)
                if (response.status == "success") {
                    _uiState.value = UpdateUiState.Success(response.updates)
                } else {
                    _uiState.value = UpdateUiState.Error("Failed to load updates.")
                }
            } catch (e: Exception) {
                _uiState.value = UpdateUiState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}