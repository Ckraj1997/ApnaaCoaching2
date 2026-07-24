package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.CbtQuestion
import com.chandan.apnaacoaching.data.CbtResponse
import com.chandan.apnaacoaching.data.EnrollResponse
import com.chandan.apnaacoaching.data.FullResultResponse
import com.chandan.apnaacoaching.data.KbcSessionResponse
import com.chandan.apnaacoaching.data.QuizSubmission
import com.chandan.apnaacoaching.data.ResultResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PracticeApiService {
    @GET("get_cbt_list_new.php")
    suspend fun getCbtList(@Query("user_id") userId: String): CbtResponse

    @FormUrlEncoded
    @POST("enroll_cbt.php")
    suspend fun enrollInTest(
        @Field("user_id") userId: String,
        @Field("cbt_id") cbtId: Int // Changed to match your PHP $_POST['cbt_id']
    ): EnrollResponse

    @GET("get_questions.php")
    suspend fun getQuestions(@Query("set_id") setId: Int): List<CbtQuestion> // <-- इसे वापस List कर दें

    @POST("submit_answers.php")
    suspend fun submitAnswers(@Body submission: QuizSubmission): Response<ResponseBody>

    @GET("get_user_result.php")
    suspend fun getUserResult(
        @Query("user_id") userId: String,
        @Query("quiz_id") quizId: Int
    ): ResultResponse

    @GET("get_full_result_api.php")
    suspend fun getFullResult(
        @Query("quiz_id") quizId: Int,
        @Query("user_id") userId: String
    ): FullResultResponse

    // Add the endpoint
    @GET("get_kbc_session.php")
    suspend fun startKbcSession(): KbcSessionResponse
}