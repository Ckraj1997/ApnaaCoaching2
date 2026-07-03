package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class StudyGroup(
    val id: Int,
    val title: String,
    val description: String,
    @SerializedName("image_url") val imageUrl: String
)