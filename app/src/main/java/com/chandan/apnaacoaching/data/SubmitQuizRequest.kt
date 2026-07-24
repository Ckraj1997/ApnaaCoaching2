package com.chandan.apnaacoaching.data

data class SubmitQuizRequest(
    val quiz_id: String,
    val user_id: String,
    val answers: Map<String, String>
)
