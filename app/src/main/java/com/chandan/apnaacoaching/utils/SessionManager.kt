package com.chandan.apnaacoaching.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun saveUser(userId: String, userName: String) {
        prefs.edit {
            putString("USER_ID", userId)
                .putString("USER_NAME", userName)
        }
    }

    fun getUserId(): String? {
        return prefs.getString("USER_ID", null)
    }

    fun getUserName(): String? {
        return prefs.getString("USER_NAME", null)
    }

    fun logout() {
        prefs.edit { clear() }
    }
}