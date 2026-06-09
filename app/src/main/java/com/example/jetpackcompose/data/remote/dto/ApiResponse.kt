package com.example.jetpackcompose.data.remote.dto

data class ApiResponse<T>(
    val author: String?,
    val statusCode: Int?,
    val message: String?,
    val data: T?
)
