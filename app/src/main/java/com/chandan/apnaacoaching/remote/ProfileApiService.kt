package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.UserProfileResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApiService {
    @GET("get_user_profile.php") // We will need to create this PHP file on Hostinger!
    suspend fun getUserProfile(@Query("user_id") userId: String): UserProfileResponse
}