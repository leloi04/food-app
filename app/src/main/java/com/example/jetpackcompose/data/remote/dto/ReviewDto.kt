package com.example.jetpackcompose.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateReviewRequest(
    val type: String = "item",
    val user: String,
    val menuItemId: String,
    val rating: Int,
    val comment: String
)

data class UpdateReviewRequest(
    val rating: Int,
    val comment: String
)

data class ReviewListRequest(
    val id: String
)

data class ReviewDto(
    @SerializedName("_id") val id: String?,
    val type: String?,
    val userId: String?,
    val user: String?,
    val avatar: String?,
    val menuItemId: String?,
    val rating: Int?,
    val comment: String?,
    val createdAt: String?,
    val updatedAt: String?
)
