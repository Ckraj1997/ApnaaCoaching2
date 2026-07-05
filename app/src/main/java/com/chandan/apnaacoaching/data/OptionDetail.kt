package com.chandan.apnaacoaching.data

data class OptionDetail(
    val opt_id: String,
    val text_en: String,
    val text_hi: String,
    val is_right: String, // "1" for true, "0" for false
    val user_selected: Int // 1 if user chose this, 0 otherwise
)
