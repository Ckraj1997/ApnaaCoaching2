package com.chandan.apnaacoaching.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {
    // True = Dark Mode, False = Light Mode
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun toggleTheme(enabled: Boolean) {
        _isDarkMode.value = enabled
    }
}