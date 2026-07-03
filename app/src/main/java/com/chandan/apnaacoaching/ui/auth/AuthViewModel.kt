package com.chandan.apnaacoaching.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.LoginRequest
import com.chandan.apnaacoaching.remote.RetrofitClient
import com.chandan.apnaacoaching.ui.auth.AuthState.Error
import com.chandan.apnaacoaching.utils.SessionManager // Make sure this matches where you put SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Upgraded to AndroidViewModel to access the application context
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // 2. Initialize the SessionManager
    private val sessionManager = SessionManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = password)
                val response = RetrofitClient.authApi.login(request)
                if (response.status == "success") {

                    // 3. PERSIST THE LOGIN: Save the user data locally to the phone!
                    val userId = response.userId ?: ""
                    val userName = response.name ?: response.firstName ?: "Student"
                    sessionManager.saveUser(userId, userName)

                    _authState.value = AuthState.Success(response)
                } else {
                    _authState.value = Error(response.message)
                }
            } catch (e: Exception) {
                _authState.value = Error("Network Error: ${e.localizedMessage}")
            }
        }
    }


    fun loginWithGoogleToken(idToken: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.authApi.loginWithGoogle(idToken)

                if (response.status == "success") {

                    // 4. PERSIST THE LOGIN: Do the same for Google Sign-In
                    val userId = response.userId ?: ""
                    val userName = response.name ?: response.firstName ?: "Student"
                    sessionManager.saveUser(userId, userName)

                    _authState.value = AuthState.Success(response)
                } else {
                    _authState.value = Error(response.message)
                }
            } catch (e: Exception) {
                _authState.value = Error("Network Error: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}