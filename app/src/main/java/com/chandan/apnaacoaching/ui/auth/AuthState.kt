package com.chandan.apnaacoaching.ui.auth

import com.chandan.apnaacoaching.data.LoginResponse

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: LoginResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}