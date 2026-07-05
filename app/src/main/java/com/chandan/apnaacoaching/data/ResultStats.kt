package com.chandan.apnaacoaching.data

data class ResultStats(
    val total: Int,
    val attempted: Int,
    val correct: Int,
    val wrong: Int,
    val unattempted: Int,
    val marks_text: String
)
