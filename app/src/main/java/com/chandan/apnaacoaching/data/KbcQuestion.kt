package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class KbcQuestion(
    val level: Int,
    @SerializedName("question_id") val questionId: String,
    @SerializedName("question_en") val questionEn: String,
    @SerializedName("question_hi") val questionHi: String?,
    @SerializedName("question_image") val questionImage: String?,
    val options: List<KbcOption> = emptyList()
)
