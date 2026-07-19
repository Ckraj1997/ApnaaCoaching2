package com.chandan.apnaacoaching.data

data class CommunityThread(
    val thread_id: String,
    val title: String,
    val description: String,
    val user_id: String,
    val first_name: String?,
    val user_pic_name: String?,
    val posted_on: String?
)

