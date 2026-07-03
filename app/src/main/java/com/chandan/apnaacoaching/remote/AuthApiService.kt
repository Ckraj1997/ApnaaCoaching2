package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.LoginRequest
import com.chandan.apnaacoaching.data.LoginResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {
    @POST("login_api.php")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @FormUrlEncoded
    @POST("google_login_api.php")
    suspend fun loginWithGoogle(
        @Field("id_token") idToken: String
    ): LoginResponse

}