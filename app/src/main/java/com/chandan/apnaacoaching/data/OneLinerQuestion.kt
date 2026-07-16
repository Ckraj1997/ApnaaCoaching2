package com.chandan.apnaacoaching.data

data class OneLinerQuestion(
    val question_id: String,
    val question_name_Hi: String?,
    val question_image_Hi: String?,
    val question: String?,
    val question_image: String?,
    val id: String, // Question relational ID
    val options: List<OneLinerOption>
)
