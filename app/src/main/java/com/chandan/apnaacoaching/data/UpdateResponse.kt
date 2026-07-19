package com.chandan.apnaacoaching.data

data class UpdateResponse(
    val status: String,
    val updates: List<UpdateItem> = emptyList()
)
