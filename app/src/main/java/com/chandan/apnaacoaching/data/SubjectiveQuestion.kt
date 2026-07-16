package com.chandan.apnaacoaching.data

data class SubjectiveQuestion(
    val sub_id: String,
    val question: String?,
    val question_hi: String?,     // Updated
    val question_img: String?,
    val question_img_hi: String?, // Updated
    val answer: String?,
    val answer_hi: String?,       // Updated
    val answer_img: String?,
    val answer_img_hi: String?    // Updated
)
