package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val status: String,
    val message: String,
    @SerializedName("user_role") val userRole: String? = null,
    val name: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    val token: String? = null,
    val email: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    val picture: String? = null,
    val dob: String? = null,
    val hasDefaultPassword: Boolean? = null,
    val phone: String? = null,
    val location: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val coins: Int? = null
)