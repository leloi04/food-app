package com.example.jetpackcompose.data.model

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Long,
    val image: String,
    val category: String,
    val averageRating: Double,
    val kitchenArea: String,
    val variants: List<Variant>,
    val toppings: List<Topping>
)

data class Variant(
    val id: String,
    val label: String,
    val price: Long
)

data class Topping(
    val id: String,
    val name: String,
    val price: Long
)
