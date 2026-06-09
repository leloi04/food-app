package com.example.jetpackcompose.data.model

data class CartItem(
    val id: String,
    val name: String,
    val image: String,
    val kitchenArea: String,
    val quantity: Int,
    val price: Long,
    val variant: Variant?,
    val toppings: List<Topping>
) {
    fun getTotalPrice(): Long {
        val basePrice = variant?.price ?: price
        val toppingsPrice = toppings.sumOf { it.price }
        return (basePrice + toppingsPrice) * quantity
    }
}
