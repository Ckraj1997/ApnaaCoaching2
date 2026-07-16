package com.chandan.apnaacoaching.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.CategoryItem
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Success(val categories: List<CategoryItem>) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

class CategoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun fetchCategories(groupId: String, levelId: String) {
        _uiState.value = CategoryUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.dashboardApi.getCategories(groupId, levelId)
                if (response.status == "success") {
                    _uiState.value = CategoryUiState.Success(response.cat)
                } else {
                    _uiState.value = CategoryUiState.Error("Failed to fetch categories.")
                }
            } catch (e: Exception) {
                _uiState.value = CategoryUiState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }
}