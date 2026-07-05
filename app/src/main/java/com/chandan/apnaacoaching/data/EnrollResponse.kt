package com.chandan.apnaacoaching.data

data class EnrollResponse(
    val status: String,
    val message: String,
    val newCoinBalance: Int? = null
)

