package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class CarouselImage(
    val id: Int,
    @SerializedName("image_url") val imageUrl: String
)