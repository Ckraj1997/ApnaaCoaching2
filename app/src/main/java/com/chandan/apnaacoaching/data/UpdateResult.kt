package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class UpdateResult(
    val id: String?,
    @SerializedName("update_title") val title: String?,
    @SerializedName("update_description") val description: String?
)
