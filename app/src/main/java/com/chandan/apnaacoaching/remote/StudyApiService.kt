package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.OneLinerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StudyApiService {
    @GET("get_oneliner.php")
    suspend fun getOneLiners(
        @Query("groupId") groupId: String,
        @Query("levelId") levelId: String,
        @Query("catId") catId: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): OneLinerResponse
}