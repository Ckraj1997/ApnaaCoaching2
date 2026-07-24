package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class StudyQuizOption(
    @SerializedName("option_id") val optionId: String,
    @SerializedName("option_text_en") val optionTextEn: String,
    @SerializedName("option_text_hi") val optionTextHi: String?,
    @SerializedName("option_image") val optionImage: String?
)
