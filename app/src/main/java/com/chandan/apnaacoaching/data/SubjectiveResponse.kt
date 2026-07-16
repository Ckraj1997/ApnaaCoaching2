package com.chandan.apnaacoaching.data

data class SubjectiveResponse(
    val status: String,
    val questions: List<SubjectiveQuestion> = emptyList()
)
