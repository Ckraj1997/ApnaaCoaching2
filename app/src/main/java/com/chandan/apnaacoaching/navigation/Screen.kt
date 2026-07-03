package com.chandan.apnaacoaching.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Practice : Screen("practice")
    object Cart : Screen("cart")
    object Content : Screen("content")
    object Account : Screen("account")
    object Settings : Screen("settings")
}