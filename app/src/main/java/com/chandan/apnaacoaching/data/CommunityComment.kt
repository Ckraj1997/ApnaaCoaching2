package com.chandan.apnaacoaching.data

data class CommunityComment(
    val com_id: String,
    val comments: String,
    val user_id: String,
    val first_name: String?,
    val user_pic_name: String?,
    val commented_on: String?
)
