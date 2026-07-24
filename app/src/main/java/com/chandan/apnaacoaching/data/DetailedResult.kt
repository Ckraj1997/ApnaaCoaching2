package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class DetailedResult(
    @SerializedName("question_id") val questionId: String,
    @SerializedName("question_en") val questionEn: String,
    @SerializedName("question_hi") val questionHi: String?,
    @SerializedName("user_selected_option_id") val userSelectedOptionId: String?,
    @SerializedName("correct_option_id") val correctOptionId: String?,
    @SerializedName("is_user_correct") val isUserCorrect: Boolean,
    @SerializedName("explanation_en") val explanationEn: String?,
    @SerializedName("explanation_hi") val explanationHi: String?,
    @SerializedName("explanation_image") val explanationImage: String?,
    val options: List<SolutionOption> = emptyList()
)
