package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class PdfResult(
    val id: String?,
    val title: String?,
    @SerializedName("Description") val description: String?
)
