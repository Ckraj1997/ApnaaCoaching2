package com.chandan.apnaacoaching.data

data class UserProfileResponse(
    val status: String,
    val message: String? = null,
    val profile: UserProfile? = null
)
