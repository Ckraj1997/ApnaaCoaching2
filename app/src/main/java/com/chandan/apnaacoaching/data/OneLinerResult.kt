package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class OneLinerResult(
    @SerializedName("question_id") val questionId: String?,
    val question: String?,
    @SerializedName("question_name_Hi") val questionHi: String?,
    val answer: String?,
    @SerializedName("answer_hi") val answerHi: String?
)
