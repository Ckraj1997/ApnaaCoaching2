package com.chandan.apnaacoaching.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Practice : Screen("practice")

    object Community : Screen("community_groups_screen")
    object Content : Screen("content")
    object Account : Screen("account")
    object Settings : Screen("settings")
}