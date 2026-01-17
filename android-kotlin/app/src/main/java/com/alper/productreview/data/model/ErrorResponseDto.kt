package com.alper.productreview.data.model

data class ErrorResponseDto(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: String,
    val path: String
)
