package com.example.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.model.CartItem
import com.example.jetpackcompose.data.model.MenuItem
import com.example.jetpackcompose.data.repository.CartRepository
import com.example.jetpackcompose.data.repository.FoodRepository
import com.example.jetpackcompose.utils.Resource
import com.example.jetpackcompose.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    val cartItems = cartRepository.getCartItems().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _editingItemDetails = MutableStateFlow<UiState<MenuItem>>(UiState.Idle)
    val editingItemDetails = _editingItemDetails.asStateFlow()

    fun fetchItemDetails(itemId: String) {
        viewModelScope.launch {
            foodRepository.getMenuItem(itemId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _editingItemDetails.value = UiState.Loading
                    is Resource.Success -> _editingItemDetails.value = UiState.Success(resource.data!!)
                    is Resource.Error -> _editingItemDetails.value = UiState.Error(resource.message ?: "Failed")
                }
            }
        }
    }

    fun clearEditingState() {
        _editingItemDetails.value = UiState.Idle
    }

    val totalItemPrice = cartItems.map { items ->
        items.sumOf { it.getTotalPrice() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun updateQuantity(item: CartItem, delta: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(item, item.quantity + delta)
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    fun updateItem(oldItem: CartItem, newItem: CartItem) {
        viewModelScope.launch {
            cartRepository.replaceCartItem(oldItem, newItem)
        }
    }
}
