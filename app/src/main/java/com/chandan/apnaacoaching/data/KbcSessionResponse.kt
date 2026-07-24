package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class KbcSessionResponse(
    val status: String,
    val message: String? = null,
    @SerializedName("kbc_questions") val kbcQuestions: List<KbcQuestion> = emptyList()
)
