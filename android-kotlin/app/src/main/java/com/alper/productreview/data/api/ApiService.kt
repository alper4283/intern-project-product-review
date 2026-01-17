package com.alper.productreview.data.api

import com.alper.productreview.data.model.*
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @GET("api/products")
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = null
    ): PageDto<ProductDto>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Long): ProductDto

    @GET("api/products/{productId}/reviews")
    suspend fun getReviews(@Path("productId") productId: Long): List<ReviewDto>

    @POST("api/products/{productId}/reviews")
    suspend fun addReview(
        @Path("productId") productId: Long,
        @Body body: AddReviewRequestDto
    ): ReviewDto
}
