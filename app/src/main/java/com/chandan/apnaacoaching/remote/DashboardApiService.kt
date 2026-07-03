package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.DashboardResponse
import retrofit2.http.GET

interface DashboardApiService {
    @GET("dashboard_api.php")
    suspend fun getDashboardData(): DashboardResponse
}