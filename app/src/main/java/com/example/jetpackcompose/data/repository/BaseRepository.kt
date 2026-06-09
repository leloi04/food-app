package com.example.jetpackcompose.data.repository

import com.example.jetpackcompose.data.remote.dto.ApiResponse
import com.example.jetpackcompose.utils.Resource
import retrofit2.Response

abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): Resource<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    if (body.data != null) {
                        Resource.Success(body.data)
                    } else if (body.statusCode == 200 || body.statusCode == 201) {
                        // For cases where data might be null but status is success (like logout)
                        @Suppress("UNCHECKED_CAST")
                        Resource.Success(Unit as T)
                    } else {
                        Resource.Error(body.message ?: "Data is null")
                    }
                } else {
                    Resource.Error("Response body is null")
                }
            } else {
                // Handle error body if available
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
