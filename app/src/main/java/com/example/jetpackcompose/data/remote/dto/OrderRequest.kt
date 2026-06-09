package com.example.jetpackcompose.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    val paymentStatus: String = "unpaid",
    val customerId: String,
    val method: String, // "ship" or "pickup"
    val payment: String, // "cod" or "transfer"
    val deliveryAddress: String?,
    val pickupTime: String?,
    val note: String?,
    val totalItemPrice: Long,
    val totalPayment: Long,
    val orderItems: List<OrderItemDto>
)

data class CompleteOrderRequest(
    val id: String
)

data class OrderItemDto(
    val kitchenArea: String? = null,
    val menuItemId: String? = null,
    val name: String? = null,
    val quantity: Int = 0,
    val price: Long = 0,
    val variant: OrderVariantDto? = null,
    val toppings: List<OrderToppingDto>? = null
)

data class OrderVariantDto(
    @SerializedName("_id") val id: String? = null,
    val size: String? = null,
    val price: Long = 0
)

data class OrderToppingDto(
    @SerializedName("_id") val id: String? = null,
    val name: String? = null,
    val price: Long = 0
)
