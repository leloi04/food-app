package com.example.jetpackcompose.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * These are the data structures INSIDE the "data" field of ApiResponse
 */

data class AuthData(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    val user: UserDto?
)

data class AccountData(
    val user: UserDto?
)

data class UserDto(
    @SerializedName("_id") val id: String?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val avatar: String?,
    val gender: String?,
    val role: RoleDto?
)

data class RoleDto(
    @SerializedName("_id") val id: String?,
    val name: String?
)

data class TokenData(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?
)
