package com.chandan.apnaacoaching.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.UserProfile
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: UserProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    fun fetchProfile(userId: String) {
        _uiState.value = ProfileState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.profileApi.getUserProfile(userId)
                if (response.status == "success" && response.profile != null) {
                    _uiState.value = ProfileState.Success(response.profile)
                } else {
                    _uiState.value =
                        ProfileState.Error(response.message ?: "Failed to load profile")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error(e.localizedMessage ?: "Network Error")
            }
        }
    }
}