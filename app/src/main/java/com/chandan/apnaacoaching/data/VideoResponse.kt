package com.chandan.apnaacoaching.data

data class VideoResponse(
    val status: String,
    val videos: List<VideoItem> = emptyList()
)
