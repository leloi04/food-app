package com.example.jetpackcompose.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.model.MenuItem
import com.example.jetpackcompose.data.repository.FoodRepository
import com.example.jetpackcompose.utils.Resource
import com.example.jetpackcompose.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val menuId: String? = savedStateHandle["menuId"]

    private val _menuItemState = MutableStateFlow<UiState<MenuItem>>(UiState.Loading)
    val menuItemState = _menuItemState.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity = _quantity.asStateFlow()

    init {
        fetchMenuItem()
    }

    private fun fetchMenuItem() {
        if (menuId == null) return
        viewModelScope.launch {
            // Usually we'd have a specific fetch by ID, but for now I'll filter from all
            foodRepository.getMenuItems(null).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val item = resource.data?.find { it.id == menuId }
                        if (item != null) {
                            _menuItemState.value = UiState.Success(item)
                        } else {
                            _menuItemState.value = UiState.Error("Item not found")
                        }
                    }
                    is Resource.Error -> _menuItemState.value = UiState.Error(resource.message ?: "Error")
                    is Resource.Loading -> _menuItemState.value = UiState.Loading
                }
            }
        }
    }

    fun incrementQuantity() { _quantity.value++ }
    fun decrementQuantity() { if (_quantity.value > 1) _quantity.value-- }
}
