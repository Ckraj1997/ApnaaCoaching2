package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.CategoryResponse
import com.chandan.apnaacoaching.data.DashboardResponse
import com.chandan.apnaacoaching.data.LevelResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApiService {
    @GET("dashboard_api.php")
    suspend fun getDashboardData(): DashboardResponse

    @GET("get_level.php")
    suspend fun getLevels(
        @Query("groupId") groupId: String
    ): LevelResponse

    @GET("get_category.php")
    suspend fun getCategories(
        @Query("groupId") groupId: String,
        @Query("levelId") levelId: String
    ): CategoryResponse
}