package com.chandan.apnaacoaching.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://apnaacoaching.in/config/api/"
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val authApi: AuthApiService = retrofit.create(AuthApiService::class.java)
    val dashboardApi: DashboardApiService = retrofit.create(DashboardApiService::class.java)

    val practiceApi: PracticeApiService = retrofit.create(PracticeApiService::class.java)

    val profileApi: ProfileApiService = retrofit.create(ProfileApiService::class.java)
}