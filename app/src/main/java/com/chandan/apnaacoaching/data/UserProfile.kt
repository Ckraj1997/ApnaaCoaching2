package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("user_id") val userId: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("middle_name") val middleName: String?,
    @SerializedName("last_name") val lastName: String,
    val email: String,
    val phone: String?,
    @SerializedName("user_pic_name") val picture: String?,
    val gender: String?,
    val city: String?,
    val state: String?,
    val coins: Int,
    val dob: String?, // <-- ADDED THIS
    val location: String?, // <-- ADDED THIS,
    val pincode: String? // <-- ADDED THIS
)
