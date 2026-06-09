package com.example.jetpackcompose.data.remote.dto

import com.example.jetpackcompose.data.model.Promotion

data class PromotionDto(
    val id: String?,
    val title: String?,
    val description: String?,
    val image: String?
)

fun PromotionDto.toDomain() = Promotion(
    id = id ?: "",
    title = title ?: "",
    description = description ?: "",
    image = image ?: ""
)
