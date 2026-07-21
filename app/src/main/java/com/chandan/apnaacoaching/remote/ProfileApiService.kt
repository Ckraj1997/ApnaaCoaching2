package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.UploadPicResponse
import com.chandan.apnaacoaching.data.UserProfile
import com.chandan.apnaacoaching.data.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ProfileApiService {
    @GET("get_user_profile.php") // We will need to create this PHP file on Hostinger!
    suspend fun getUserProfile(@Query("user_id") userId: String): UserProfileResponse

     @POST("update_profile.php")
     suspend fun updateProfile(@Body profile: UserProfile): UserProfileResponse

    // Add this inside your API Interface
    @Multipart
    @POST("upload_profile_pic.php") // Update with your actual path
    suspend fun uploadProfilePic(
        @Part("user_id") userId: RequestBody,
        @Part profile_pic: MultipartBody.Part
    ): UploadPicResponse
}