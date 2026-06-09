package com.example.jetpackcompose.data.remote.dto

data class UpdateUserRequest(
    val email: String,
    val name: String,
    val phone: String,
    val gender: String,
    val avatar: String
)

data class UpdatePasswordRequest(
    val email: String,
    val oldPassword: String,
    val newPassword: String
)
