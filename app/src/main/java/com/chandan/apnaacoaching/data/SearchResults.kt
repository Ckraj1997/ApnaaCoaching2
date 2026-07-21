package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class SearchResults(
    val updates: List<UpdateResult> = emptyList(),
    val videos: List<VideoResult> = emptyList(),
    val pdfs: List<PdfResult> = emptyList(),
    @SerializedName("long_questions") val longQuestions: List<SubjectiveResult> = emptyList(),
    val oneliners: List<OneLinerResult> = emptyList()
)
