package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class SubmitQuizResponse(
    val status: String,
    val message: String? = null,
    val score: Int? = null,
    @SerializedName("max_score") val maxScore: Int? = null
)
