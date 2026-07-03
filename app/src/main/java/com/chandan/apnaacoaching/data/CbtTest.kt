package com.chandan.apnaacoaching.data

import com.google.gson.annotations.SerializedName

data class CbtTest(
    val id: Int,
    val heading: String,
    val tagline: String,
    val logo: String,
    val timeDuration: Int,
    val minusPoint: Int,
    val plusPoint: Int,
    val sysName: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    val isSubmitted: Boolean,
    val isEnrolled: Boolean,
    val entryFee: Int,
    @SerializedName("test_status") val testStatus: String // "live", "mock", or "upcoming"
)