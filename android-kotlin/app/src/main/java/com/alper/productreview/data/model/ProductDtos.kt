package com.alper.productreview.data.model

data class ProductDto(
    val id: Long,
    val name: String,
    val category: String,
    val price: Double,
    val averageRating: Double,
    val reviewCount: Long,
    val description: String? = null
)

data class PageDto<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int
    // pageable exists too, but you don't need it for UI
)
