package com.example.jetpackcompose.data.repository

import com.example.jetpackcompose.data.datastore.AuthPreference
import com.example.jetpackcompose.data.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val authPreference: AuthPreference
) {
    fun getCartItems(): Flow<List<CartItem>> = authPreference.getCartItems()

    suspend fun addToCart(item: CartItem) {
        val currentItems = authPreference.getCartItems().first().toMutableList()
        // If same id, variant and toppings, increase quantity
        val existingIndex = currentItems.indexOfFirst { 
            it.id == item.id && it.variant?.id == item.variant?.id && it.toppings == item.toppings 
        }
        
        if (existingIndex != -1) {
            val existingItem = currentItems[existingIndex]
            currentItems[existingIndex] = existingItem.copy(quantity = existingItem.quantity + item.quantity)
        } else {
            currentItems.add(item)
        }
        authPreference.saveCartItems(currentItems)
    }

    suspend fun updateQuantity(item: CartItem, newQuantity: Int) {
        val currentItems = authPreference.getCartItems().first().toMutableList()
        val index = currentItems.indexOfFirst { 
            it.id == item.id && it.variant?.id == item.variant?.id && it.toppings == item.toppings 
        }
        if (index != -1) {
            if (newQuantity > 0) {
                currentItems[index] = currentItems[index].copy(quantity = newQuantity)
            } else {
                currentItems.removeAt(index)
            }
            authPreference.saveCartItems(currentItems)
        }
    }

    suspend fun removeFromCart(item: CartItem) {
        val currentItems = authPreference.getCartItems().first().toMutableList()
        currentItems.removeAll { 
            it.id == item.id && it.variant?.id == item.variant?.id && it.toppings == item.toppings 
        }
        authPreference.saveCartItems(currentItems)
    }

    suspend fun replaceCartItem(oldItem: CartItem, newItem: CartItem) {
        val currentItems = authPreference.getCartItems().first().toMutableList()
        val index = currentItems.indexOfFirst { 
            it.id == oldItem.id && it.variant?.id == oldItem.variant?.id && it.toppings == oldItem.toppings 
        }
        if (index != -1) {
            currentItems[index] = newItem
            authPreference.saveCartItems(currentItems)
        }
    }

    suspend fun clearCart() {
        authPreference.saveCartItems(emptyList())
    }
}
