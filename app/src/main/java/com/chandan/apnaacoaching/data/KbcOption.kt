package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class KbcOption(
    @SerializedName("option_id") val optionId: String,
    @SerializedName("text_en") val textEn: String,
    @SerializedName("text_hi") val textHi: String?,
    @SerializedName("is_correct") val isCorrect: Boolean,
    @SerializedName("option_image") val optionImage: String?,
    var isHidden: Boolean = false // Used for 50:50 Lifeline
)
