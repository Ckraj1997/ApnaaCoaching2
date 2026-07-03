package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class LatestUpdate(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    @SerializedName("image_url") val imageUrl: String
)