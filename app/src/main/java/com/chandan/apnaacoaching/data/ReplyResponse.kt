package com.chandan.apnaacoaching.data

data class ReplyResponse(
    val status: String,
    val thread: CommunityThread?,
    val replies: List<CommunityReply>?
)
