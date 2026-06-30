package com.example.jetpackcompose.data.remote.dto

import com.example.jetpackcompose.data.model.MenuItem
import com.example.jetpackcompose.data.model.Topping
import com.example.jetpackcompose.data.model.Variant
import com.google.gson.annotations.SerializedName

data class MenuItemDto(
    @SerializedName("_id") val id: String?,
    val name: String?,
    val description: String?,
    val price: Long?,
    val image: String?,
    val category: String?,
    val averageRating: Double?,
    val kitchenArea: String?,
    val status: String?,
    val variants: List<VariantDto>?,
    val toppings: List<ToppingDto>?
)

data class VariantDto(
    @SerializedName("_id") val id: String?,
    val size: String?,
    val price: Long?
)

data class ToppingDto(
    @SerializedName("_id") val id: String?,
    val name: String?,
    val price: Long?
)

fun MenuItemDto.toDomain() = MenuItem(
    id = id ?: "",
    name = name ?: "",
    description = description ?: "",
    price = price ?: 0L,
    image = image ?: "",
    category = category ?: "",
    averageRating = averageRating ?: 0.0,
    kitchenArea = kitchenArea ?: "HOT",
    status = status ?: "available",
    variants = variants?.map { it.toDomain() } ?: emptyList(),
    toppings = toppings?.map { it.toDomain() } ?: emptyList()
)

fun VariantDto.toDomain() = Variant(
    id = id ?: "",
    label = size ?: "",
    price = price ?: 0L
)

fun ToppingDto.toDomain() = Topping(
    id = id ?: "",
    name = name ?: "",
    price = price ?: 0L
)
