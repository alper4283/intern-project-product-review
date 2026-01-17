package com.alper.productreview.data.model

data class ReviewDto(
    val id: Long,
    val rating: Int,
    val comment: String?,
    val username: String,
    val createdAt: String
)

data class AddReviewRequestDto(val rating: Int, val comment: String?)
