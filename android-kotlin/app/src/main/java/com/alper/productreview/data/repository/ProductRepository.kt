package com.alper.productreview.data.repository

import com.alper.productreview.data.api.ApiService
import com.alper.productreview.data.model.AddReviewRequestDto
import com.alper.productreview.data.model.PageDto
import com.alper.productreview.data.model.ProductDto

class ProductRepository(private val api: ApiService) {
    suspend fun getProducts(page: Int, size: Int, sort: String?): PageDto<ProductDto> {
        return api.getProducts(page = page, size = size, sort = sort)
    }

    suspend fun getProduct(id: Long): ProductDto = api.getProduct(id)

    suspend fun getReviews(productId: Long) = api.getReviews(productId)
    suspend fun addReview(productId: Long, rating: Int, comment: String?) =
        api.addReview(productId, AddReviewRequestDto(rating = rating, comment = comment))

}
