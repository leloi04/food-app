package com.example.jetpackcompose.data.remote.api

import com.example.jetpackcompose.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ReviewApi {
    @POST("/reviews")
    suspend fun createReview(@Body request: CreateReviewRequest): Response<ApiResponse<ReviewDto>>

    @PATCH("/reviews/{id}")
    suspend fun updateReview(@Path("id") id: String, @Body request: UpdateReviewRequest): Response<ApiResponse<ReviewDto>>

    @DELETE("/reviews/{id}")
    suspend fun deleteReview(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("/reviews/comment-list")
    suspend fun getReviewList(@Body request: ReviewListRequest): Response<ApiResponse<List<ReviewDto>>>
}
