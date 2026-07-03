package com.chandan.apnaacoaching.data

data class CbtResponse(
    val status: String,
    val message: String? = null,
    val cbts: List<CbtTest> = emptyList()
)