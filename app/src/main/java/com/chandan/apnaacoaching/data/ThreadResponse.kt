package com.chandan.apnaacoaching.data

data class ThreadResponse(
    val status: String,
    val threads: List<CommunityThread>?,
    val has_more: Boolean,       // NEW
    val current_page: Int
)
