package com.chandan.apnaacoaching.remote

import com.chandan.apnaacoaching.data.CommentResponse
import com.chandan.apnaacoaching.data.GroupResponse
import com.chandan.apnaacoaching.data.ReplyResponse
import com.chandan.apnaacoaching.data.ThreadResponse
import com.chandan.apnaacoaching.data.ToggleLikeResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CommunityApiService {
    @GET("api_community.php?action=get_threads")
    suspend fun getThreads(
        @Query("group_id") groupId: String,
        @Query("page") page: Int // NEW: Request specific page
    ): Response<ThreadResponse>

    @FormUrlEncoded
    @POST("api_community.php?action=create_thread")
    suspend fun createThread(
        @Field("group_id") groupId: String,
        @Field("user_id") userId: String, // Pass the already logged-in user ID here!
        @Field("title") title: String,
        @Field("description") description: String
    ): Response<ResponseBody>

    // FIX: Added userId parameter so the server knows who is asking!
    @GET("api_community.php?action=get_replies")
    suspend fun getReplies(
        @Query("thread_id") threadId: String,
        @Query("user_id") userId: String
    ): Response<ReplyResponse>

    // NEW API CALL
    @FormUrlEncoded
    @POST("api_community.php?action=toggle_like")
    suspend fun toggleLike(
        @Field("reply_id") replyId: String,
        @Field("user_id") userId: String
    ): Response<ToggleLikeResponse>

    @FormUrlEncoded
    @POST("api_community.php?action=create_reply")
    suspend fun createReply(
        @Field("user_id") userId: String,
        @Field("reply") reply: String,
        @Field("thread_id") threadId: String
    ): Response<ResponseBody>

    @GET("api_community.php?action=get_groups")
    suspend fun getGroups(): Response<GroupResponse>


    @GET("api_community.php?action=get_comments")
    suspend fun getComments(@Query("reply_id") replyId: String): Response<CommentResponse>

    @FormUrlEncoded
    @POST("api_community.php?action=create_comment")
    suspend fun createComment(
        @Field("user_id") userId: String,
        @Field("reply_id") replyId: String,
        @Field("thread_id") threadId: String,
        @Field("comments") comments: String
    ): Response<ResponseBody>
}