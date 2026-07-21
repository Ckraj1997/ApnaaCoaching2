package com.chandan.apnaacoaching.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class LanguageViewModel : ViewModel() {
    // True = Hindi ("hi"), False = English ("en")
    private val _isHindi = MutableStateFlow(false)
    val isHindi = _isHindi.asStateFlow()

    fun toggleLanguage(toHindi: Boolean) {
        _isHindi.value = toHindi
    }
}