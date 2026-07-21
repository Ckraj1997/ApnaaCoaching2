package com.chandan.apnaacoaching.data
data class SearchResponse(
    val status: String,
    val message: String? = null,
    val results: SearchResults? = null
)
