package com.chandan.apnaacoaching.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.CbtTest
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PracticeState {
    object Loading : PracticeState()
    object Success : PracticeState()
    data class Error(val message: String) : PracticeState()
}

class PracticeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeState>(PracticeState.Loading)
    val uiState: StateFlow<PracticeState> = _uiState.asStateFlow()

    // Filtered Lists
    private val _liveTests = MutableStateFlow<List<CbtTest>>(emptyList())
    val liveTests: StateFlow<List<CbtTest>> = _liveTests.asStateFlow()

    private val _mockTests = MutableStateFlow<List<CbtTest>>(emptyList())
    val mockTests: StateFlow<List<CbtTest>> = _mockTests.asStateFlow()

    fun fetchTests(userId: String) {
        _uiState.value = PracticeState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.practiceApi.getCbtList(userId)

                if (response.status == "success") {
                    // THE MAGIC FILTER: Automatically split the lists based on PHP time logic!
                    _liveTests.value = response.cbts.filter { it.testStatus == "live" }
                    _mockTests.value =
                        response.cbts.filter { it.testStatus == "mock" || it.testStatus == "upcoming" }

                    _uiState.value = PracticeState.Success
                } else {
                    _uiState.value = PracticeState.Error(response.message ?: "Failed to load tests")
                }
            } catch (e: Exception) {
                _uiState.value = PracticeState.Error(e.localizedMessage ?: "Network Error")
            }
        }
    }

    fun enrollInTest(userId: String, cbtId: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Call the API with the new parameter name
                val response = RetrofitClient.practiceApi.enrollInTest(userId, cbtId)

                // Send the message back to the UI to show in a Toast
                onResult(response.message)

                // If successful, re-fetch the tests so the button instantly updates to "Start Test"
                if (response.status == "success") {
                    fetchTests(userId)

                    // Optional: If you have a global User state, you could also update
                    // the user's coin balance here using response.newCoinBalance!
                }
            } catch (e: Exception) {
                onResult("Network error: $e")
            }
        }
    }
}