package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class ScoreData(
    val score: Int,
    @SerializedName("total_score") val totalScore: Int
)
