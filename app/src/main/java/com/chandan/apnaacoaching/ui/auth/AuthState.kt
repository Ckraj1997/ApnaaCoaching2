package com.chandan.apnaacoaching.ui.auth

import androidx.annotation.StringRes
import com.chandan.apnaacoaching.data.LoginResponse

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: LoginResponse) : AuthState()
    // Updated to handle both local XML strings and dynamic server strings
    data class Error(
        @StringRes val messageId: Int? = null,
        val apiMessage: String? = null
    ) : AuthState()
}