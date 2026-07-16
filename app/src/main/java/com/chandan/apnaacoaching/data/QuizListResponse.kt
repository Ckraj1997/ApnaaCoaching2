package com.chandan.apnaacoaching.data

data class QuizListResponse(
    val status: String,
    val message: String? = null,
    val quizzes: List<QuizItem> = emptyList()
)
