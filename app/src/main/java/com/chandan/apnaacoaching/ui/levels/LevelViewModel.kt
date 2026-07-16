package com.chandan.apnaacoaching.ui.levels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.LevelItem
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LevelUiState {
    object Loading : LevelUiState()
    data class Success(val levels: List<LevelItem>) : LevelUiState()
    data class Error(val message: String) : LevelUiState()
}

class LevelViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LevelUiState>(LevelUiState.Loading)
    val uiState: StateFlow<LevelUiState> = _uiState.asStateFlow()

    fun fetchLevels(groupId: String) {
        _uiState.value = LevelUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.dashboardApi.getLevels(groupId)
                if (response.status == "success") {
                    _uiState.value = LevelUiState.Success(response.level)
                } else {
                    _uiState.value = LevelUiState.Error("Failed to fetch levels.")
                }
            } catch (e: Exception) {
                _uiState.value = LevelUiState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }
}