package com.chandan.apnaacoaching.data

data class CommentResponse(
    val status: String,
    val reply: CommunityReply?, // The parent reply
    val comments: List<CommunityComment>?
)
