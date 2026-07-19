package com.chandan.apnaacoaching.data

data class PdfResponse(
    val status: String,
    val pdfs: List<PdfItem> = emptyList()
)
