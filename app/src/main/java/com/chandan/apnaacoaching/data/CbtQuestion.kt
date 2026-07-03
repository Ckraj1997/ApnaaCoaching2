package com.chandan.apnaacoaching.data

data class CbtQuestion(
    val id: String,                // <-- Int से String किया
    val que_hi: String,
    val que_En: String,
    val que_img_Hi: String?,
    val que_img_En: String?,
    val answer_hi: List<String>,
    val answer_en: List<String>,
    val answer_img_hi: List<String?>,
    val answer_img_en: List<String?>,
    val answers_id: List<String>,  // <-- List<Int> से List<String> किया
    val right_index: Int?,
    val right_id: String?          // <-- Int से String किया
)
