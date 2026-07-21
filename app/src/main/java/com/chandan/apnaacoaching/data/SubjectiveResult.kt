package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class SubjectiveResult(
    val id: String?,
    val question: String?,
    val answer: String?,
    @SerializedName("question_hi") val questionHi: String?,
    @SerializedName("answer_hi") val answerHi: String?
)
