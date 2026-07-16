package com.chandan.apnaacoaching.data

data class OneLinerResponse(
    val status: String,
    val message: String? = null,
    val total_questions: Int = 0,
    val data: List<OneLinerQuestion> = emptyList()
)
