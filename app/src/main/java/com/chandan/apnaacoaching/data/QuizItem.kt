package com.chandan.apnaacoaching.data

data class QuizItem(
    val quiz_id: Int,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val entry_fee: Int,
    val pluspoints: Int,
    val minuspoints: Int,
    val isPlayed: Boolean
)