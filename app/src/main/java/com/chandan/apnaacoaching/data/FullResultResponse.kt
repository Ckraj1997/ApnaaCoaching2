package com.chandan.apnaacoaching.data

data class FullResultResponse(
    val status: String,
    val message: String?,
    val release_time: String?, // Used if status == "awaiting"
    val stats: ResultStats?,
    val test_config: TestConfig?,
    val ranking: List<RankingItem>?,
    val details: List<QuestionDetail>?
)
