package com.chandan.apnaacoaching.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.SearchResults
import com.chandan.apnaacoaching.remote.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: SearchResults) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
    object Empty : SearchUiState()
}

class SearchViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel() // Cancel previous search if user is still typing

        if (query.length < 3) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500.milliseconds) // Debounce: wait 500ms after user stops typing
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.value = SearchUiState.Loading
        try {
            val response = RetrofitClient.dashboardApi.globalSearch(query)
            if (response.status == "success" && response.results != null) {

                // Check if ALL lists are empty
                val isEmpty = response.results.updates.isEmpty() &&
                        response.results.videos.isEmpty() &&
                        response.results.pdfs.isEmpty() &&
                        response.results.longQuestions.isEmpty() &&
                        response.results.oneliners.isEmpty()

                if (isEmpty) {
                    _uiState.value = SearchUiState.Empty
                } else {
                    _uiState.value = SearchUiState.Success(response.results)
                }
            } else {
                _uiState.value = SearchUiState.Error(response.message ?: "Search failed")
            }
        } catch (e: Exception) {
            _uiState.value = SearchUiState.Error("Network error: ${e.localizedMessage}")
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = SearchUiState.Idle
    }
}