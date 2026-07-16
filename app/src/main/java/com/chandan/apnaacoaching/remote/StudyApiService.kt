package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.OneLinerResponse
import com.chandan.apnaacoaching.data.QuizListResponse
import com.chandan.apnaacoaching.data.SubjectiveResponse
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

    @GET("get_long_questions.php")
    suspend fun getSubjectiveQuestions(
        @Query("group_id") groupId: String,
        @Query("level_id") levelId: String,
        @Query("cat_id") catId: String
    ): SubjectiveResponse

    @GET("get_quiz_list.php")
    suspend fun getQuizList(
        @Query("user_id") userId: String,
        @Query("cat_id") catId: String,
        @Query("level_id") levelId: String,
        @Query("group_id") groupId: String
    ): QuizListResponse
}