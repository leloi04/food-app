package com.example.jetpackcompose.data.repository

import com.example.jetpackcompose.data.model.MenuItem
import com.example.jetpackcompose.data.model.Promotion
import com.example.jetpackcompose.data.remote.api.ApiService
import com.example.jetpackcompose.data.remote.dto.CompleteOrderRequest
import com.example.jetpackcompose.data.remote.dto.CreateOrderRequest
import com.example.jetpackcompose.data.remote.dto.OrderResponse
import com.example.jetpackcompose.data.remote.dto.toDomain
import com.example.jetpackcompose.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface FoodRepository {
    fun getActivePromotions(): Flow<Resource<List<Promotion>>>
    fun getCategories(): Flow<Resource<List<String>>>
    fun getMenuItems(category: String?): Flow<Resource<List<MenuItem>>>
    fun getMenuItem(id: String): Flow<Resource<MenuItem>>
    fun createPreOrder(request: CreateOrderRequest): Flow<Resource<Any>>
    fun getUncompletedOrders(): Flow<Resource<List<OrderResponse>>>
    fun getCompletedOrders(): Flow<Resource<List<OrderResponse>>>
    fun getCancelledOrders(): Flow<Resource<List<OrderResponse>>>
    fun completePreOrder(id: String): Flow<Resource<Unit>>
}

class FoodRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BaseRepository(), FoodRepository {

    override fun getActivePromotions() = flow {
        emit(Resource.Loading())
        val result = safeApiCall { apiService.getActivePromotions() }
        if (result is Resource.Success) {
            emit(Resource.Success(result.data?.map { it.toDomain() } ?: emptyList()))
        } else {
            emit(Resource.Error(result.message ?: "Unknown Error"))
        }
    }

    override fun getCategories() = flow {
        emit(Resource.Loading())
        val result = safeApiCall { apiService.getCategories() }
        if (result is Resource.Success) {
            emit(Resource.Success(result.data ?: emptyList()))
        } else {
            emit(Resource.Error(result.message ?: "Unknown Error"))
        }
    }

    override fun getMenuItems(category: String?) = flow {
        emit(Resource.Loading())
        val result = safeApiCall { apiService.getMenuItems(category) }
        if (result is Resource.Success) {
            emit(Resource.Success(result.data?.map { it.toDomain() } ?: emptyList()))
        } else {
            emit(Resource.Error(result.message ?: "Unknown Error"))
        }
    }

    override fun getMenuItem(id: String) = flow {
        emit(Resource.Loading())
        val result = safeApiCall { apiService.getMenuItem(id) }
        if (result is Resource.Success) {
            emit(Resource.Success(result.data!!.toDomain()))
        } else {
            emit(Resource.Error(result.message ?: "Unknown Error"))
        }
    }

    override fun createPreOrder(request: CreateOrderRequest) = flow {
        emit(Resource.Loading())
        emit(safeApiCall { apiService.createPreOrder(request) })
    }

    override fun getUncompletedOrders() = flow {
        emit(Resource.Loading())
        emit(safeApiCall { apiService.getUncompletedOrders() })
    }

    override fun getCompletedOrders() = flow {
        emit(Resource.Loading())
        emit(safeApiCall { apiService.getCompletedOrders() })
    }

    override fun getCancelledOrders() = flow {
        emit(Resource.Loading())
        emit(safeApiCall { apiService.getCancelledOrders() })
    }

    override fun completePreOrder(id: String) = flow {
        emit(Resource.Loading())
        emit(safeApiCall { apiService.completePreOrder(CompleteOrderRequest(id)) })
    }
}
