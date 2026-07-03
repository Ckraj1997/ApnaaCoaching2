package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    val status: String,
    @SerializedName("carousel_images") val carouselImages: List<CarouselImage> = emptyList(),
    @SerializedName("study_groups") val studyGroups: List<StudyGroup> = emptyList(),
    @SerializedName("latest_updates") val latestUpdates: List<LatestUpdate> = emptyList()
)