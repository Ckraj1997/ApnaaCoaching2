package com.chandan.apnaacoaching.data

data class StudyQuizResponse(
    val status: String,
    val message: String? = null,
    val questions: List<StudyQuizQuestion> = emptyList()
)
