package com.chandan.apnaacoaching.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    background = WhiteOff, // Uses our soft off-white
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black
)

val DarkColors = darkColorScheme(
    primary = PrimaryBlueDark,
    onPrimary = Color.White,
    background = Charcoal, // Uses our soft charcoal
    onBackground = WhiteOff,
    surface = Color(0xFF2D2D2D), // Slightly lighter than background for depth
    onSurface = WhiteOff
)

@Composable
fun ApnaaCoachingTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Your existing typography
        content = content
    )
}
