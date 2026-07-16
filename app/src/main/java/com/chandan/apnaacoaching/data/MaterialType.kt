package com.chandan.apnaacoaching.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class MaterialType(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconTint: Color
)
