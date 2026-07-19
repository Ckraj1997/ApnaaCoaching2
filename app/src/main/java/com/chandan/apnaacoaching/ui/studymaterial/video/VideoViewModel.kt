package com.chandan.apnaacoaching.ui.studymaterial.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.VideoItem
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class VideoUiState {
    object Loading : VideoUiState()
    data class Success(val videos: List<VideoItem>) : VideoUiState()
    data class Error(val message: String) : VideoUiState()
}

class VideoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    fun fetchVideos(groupId: String, levelId: String, catId: String) {
        _uiState.value = VideoUiState.Loading

        viewModelScope.launch {
            try {
                val response = RetrofitClient.StudyApi.getVideos(groupId, levelId, catId)
                if (response.status == "success") {
                    _uiState.value = VideoUiState.Success(response.videos)
                } else {
                    _uiState.value = VideoUiState.Error("Failed to load videos.")
                }
            } catch (e: Exception) {
                _uiState.value = VideoUiState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}