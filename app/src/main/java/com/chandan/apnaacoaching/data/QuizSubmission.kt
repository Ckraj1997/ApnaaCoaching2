package com.chandan.apnaacoaching.data

data class QuizSubmission(
    val user_id: String,
    val quiz_id: Int,
    val answers: List<Int?>, // List of selected answer IDs (null if not answered)
    val statuses: List<String>, // e.g., "answered", "not_answered"
    val question_number: List<Int> // List of Question IDs
)
