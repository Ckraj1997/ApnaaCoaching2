package com.chandan.apnaacoaching.data

data class CommunityReply(
    val reply_id: String,
    val reply: String,
    val user_id: String,
    val first_name: String?,
    val user_pic_name: String?,
    val replied_on: String?,
    val like_count: Int = 0,          // NEW
    val is_liked_by_me: Boolean = false // NEW
)
