package com.example.jetpackcompose.data.remote.dto

import com.example.jetpackcompose.data.model.Category

data class CategoryDto(
    val id: String?,
    val name: String?
)

fun CategoryDto.toDomain() = Category(
    id = id ?: "",
    name = name ?: ""
)
