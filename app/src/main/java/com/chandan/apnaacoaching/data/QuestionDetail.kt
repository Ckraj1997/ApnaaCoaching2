package com.chandan.apnaacoaching.data

data class QuestionDetail(
    val q_no: Int,
    val status: String,
    val que_en: String,
    val que_hi: String,
    val options: List<OptionDetail>,
    val exp_en: String,
    val exp_hi: String
)
