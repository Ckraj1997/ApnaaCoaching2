package com.chandan.apnaacoaching.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.UserProfile
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: UserProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

// Add a specific state for the update process
sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    object Success : UpdateState()
    data class Error(val message: String) : UpdateState()
}

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val _uploadPicState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val uploadPicState: StateFlow<UpdateState> = _uploadPicState.asStateFlow()


    fun uploadProfilePicture(context: Context, userId: String, uri: Uri) {
        _uploadPicState.value = UpdateState.Loading
        viewModelScope.launch {
            try {
                // 1. Convert Uri to a temporary File
                val file = getFileFromUri(context, uri)

                // 2. Prepare Multipart request
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("profile_pic", file.name, requestFile)
                val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                // 3. Make the API Call
                val response = RetrofitClient.profileApi.uploadProfilePic(userIdBody, body)

                if (response.status == "success") {
                    _uploadPicState.value = UpdateState.Success
                    fetchProfile(userId) // Refresh profile data to show new image
                } else {
                    _uploadPicState.value = UpdateState.Error(response.message ?: "Upload failed")
                }
            } catch (e: Exception) {
                _uploadPicState.value = UpdateState.Error(e.localizedMessage ?: "Network Error")
            }
        }
    }
    fun fetchProfile(userId: String) {
        _uiState.value = ProfileState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.profileApi.getUserProfile(userId)
                if (response.status == "success" && response.profile != null) {
                    _uiState.value = ProfileState.Success(response.profile)
                } else {
                    _uiState.value = ProfileState.Error(response.message ?: "Failed to load profile")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error(e.localizedMessage ?: "Network Error")
            }
        }
    }

    fun updateProfile(profile: UserProfile) {
        _updateState.value = UpdateState.Loading
        viewModelScope.launch {
            try {


                val response = RetrofitClient.profileApi.updateProfile(profile)
                if (response.status == "success") {
                    _updateState.value = UpdateState.Success
                    fetchProfile(profile.userId) // Refresh the data in the main screen
                } else {
                    _updateState.value = UpdateState.Error(response.message ?: "Update failed")
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error(e.localizedMessage ?: "Network Error")
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateState.Idle
    }

    // Helper function to copy Uri to a temporary cache file
    private fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_profile_pic_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return tempFile
    }
}