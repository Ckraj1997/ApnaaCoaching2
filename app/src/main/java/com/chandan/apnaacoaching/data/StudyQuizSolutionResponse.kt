package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class StudyQuizSolutionResponse(
    val status: String,
    val message: String? = null,
    @SerializedName("score_data") val scoreData: ScoreData? = null,
    @SerializedName("detailed_results") val detailedResults: List<DetailedResult> = emptyList()
)
