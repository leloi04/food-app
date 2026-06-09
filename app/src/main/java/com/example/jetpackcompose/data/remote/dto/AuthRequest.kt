package com.example.jetpackcompose.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

data class GoogleLoginRequest(
    val email: String,
    val name: String,
    val picture: String
)
