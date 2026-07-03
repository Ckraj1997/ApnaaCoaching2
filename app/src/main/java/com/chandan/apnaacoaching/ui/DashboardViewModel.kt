package com.chandan.apnaacoaching.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.DashboardResponse
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardResponse) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {

        fetchDashboardData()
    }

    fun fetchDashboardData() {
        _uiState.value = DashboardState.Loading
        viewModelScope.launch {
            try {

                val response = RetrofitClient.dashboardApi.getDashboardData()
                if (response.status == "success") {
                    _uiState.value = DashboardState.Success(response)
                } else {
                    _uiState.value = DashboardState.Error("Server returned an error.")
                }
            } catch (e: Exception) {

                _uiState.value = DashboardState.Error(e.localizedMessage ?: "Unknown Network Error")
            }
        }
    }
}