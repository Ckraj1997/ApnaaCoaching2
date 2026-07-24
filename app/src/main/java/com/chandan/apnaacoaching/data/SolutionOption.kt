package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class SolutionOption(
    @SerializedName("option_id") val optionId: String,
    @SerializedName("text_en") val textEn: String,
    @SerializedName("text_hi") val textHi: String?,
    @SerializedName("is_correct") val isCorrect: Boolean,
    @SerializedName("is_selected_by_user") val isSelectedByUser: Boolean
)
