package com.chandan.apnaacoaching.data

data class RankingItem(
    val user_id: String,
    val name: String,
    val correct: Int,
    val wrong: Int,
    val marks_val: Float,
    val marks_text: String
)
