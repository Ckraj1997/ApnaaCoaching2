package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.OneLinerResponse
import com.chandan.apnaacoaching.data.PdfResponse
import com.chandan.apnaacoaching.data.QuizListResponse
import com.chandan.apnaacoaching.data.StudyQuizResponse
import com.chandan.apnaacoaching.data.StudyQuizSolutionResponse
import com.chandan.apnaacoaching.data.SubjectiveResponse
import com.chandan.apnaacoaching.data.SubmitQuizRequest
import com.chandan.apnaacoaching.data.SubmitQuizResponse
import com.chandan.apnaacoaching.data.UpdateResponse
import com.chandan.apnaacoaching.data.VideoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET("get_videos.php")
    suspend fun getVideos(
        @Query("group_id") groupId: String,
        @Query("level_id") levelId: String,
        @Query("cat_id") catId: String
    ): VideoResponse

    @GET("get_pdfs.php") // Ensure this matches your actual PHP filename
    suspend fun getPdfs(
        @Query("group_id") groupId: String,
        @Query("level_id") levelId: String,
        @Query("cat_id") catId: String
    ): PdfResponse

    @GET("get_updates.php")
    suspend fun getUpdates(
        @Query("group_id") groupId: String,
        @Query("level_id") levelId: String,
        @Query("cat_id") catId: String
    ): UpdateResponse


    @GET("get_study_quiz_questions.php")
    suspend fun getStudyQuizQuestions(
        @Query("quiz_id") quizId: String
    ): StudyQuizResponse

    @POST("submit_study_quiz.php")
    suspend fun submitStudyQuiz(
        @Body request: SubmitQuizRequest
    ): SubmitQuizResponse

    @GET("get_study_quiz_solution.php")
    suspend fun getStudyQuizSolution(
        @Query("quiz_id") quizId: String,
        @Query("user_id") userId: String
    ): StudyQuizSolutionResponse
}