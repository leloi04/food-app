package com.example.jetpackcompose.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.datastore.AuthPreference
import com.example.jetpackcompose.data.model.CartItem
import com.example.jetpackcompose.data.model.MenuItem
import com.example.jetpackcompose.data.model.Promotion
import com.example.jetpackcompose.data.model.Topping
import com.example.jetpackcompose.data.model.Variant
import com.example.jetpackcompose.data.remote.dto.UserDto
import com.example.jetpackcompose.data.repository.CartRepository
import com.example.jetpackcompose.data.repository.FoodRepository
import com.example.jetpackcompose.data.repository.LocationRepository
import com.example.jetpackcompose.utils.Resource
import com.example.jetpackcompose.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authPreference: AuthPreference,
    private val foodRepository: FoodRepository,
    private val cartRepository: CartRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val user = authPreference.userData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserDto(null, null, null, null, null, null, null)
    )

    val locationState = locationRepository.getLocationState()

    private val _promotionsState = MutableStateFlow<UiState<List<Promotion>>>(UiState.Loading)
    val promotionsState = _promotionsState.asStateFlow()

    private val _categoriesState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val categoriesState = _categoriesState.asStateFlow()

    private val _allMenuItems = MutableStateFlow<UiState<List<MenuItem>>>(UiState.Loading)

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    val filteredMenuItems: StateFlow<UiState<List<MenuItem>>> = combine(
        _allMenuItems, 
        _selectedCategory
    ) { state, category ->
        when (state) {
            is UiState.Success -> {
                val filtered = if (category == "All") {
                    state.data
                } else {
                    state.data.filter { it.category.equals(category, ignoreCase = true) }
                }
                UiState.Success(filtered)
            }
            else -> state
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    init {
        loadData()
    }

    fun loadData() {
        fetchPromotions()
        fetchCategories()
        fetchMenuItems()
    }

    fun startLocationUpdate() {
        viewModelScope.launch {
            locationRepository.startLocationUpdate()
        }
    }

    fun checkGpsAndStartLocation(activity: Activity) {
        locationRepository.checkGpsSettings(activity) {
            startLocationUpdate()
        }
    }

    fun onLocationPermissionDenied() {
        locationRepository.onPermissionDenied()
    }

    fun refreshLocation() {
        viewModelScope.launch {
            locationRepository.refreshLocation()
        }
    }

    private fun fetchPromotions() {
        viewModelScope.launch {
            foodRepository.getActivePromotions().collect { resource ->
                _promotionsState.value = when (resource) {
                    is Resource.Success -> UiState.Success(resource.data ?: emptyList())
                    is Resource.Error -> UiState.Error(resource.message ?: "Error")
                    is Resource.Loading -> UiState.Loading
                }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            foodRepository.getCategories().collect { resource ->
                _categoriesState.value = when (resource) {
                    is Resource.Success -> {
                        val list = mutableListOf("All")
                        list.addAll(resource.data ?: emptyList())
                        UiState.Success(list)
                    }
                    is Resource.Error -> UiState.Error(resource.message ?: "Error")
                    is Resource.Loading -> UiState.Loading
                }
            }
        }
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun addToCart(item: MenuItem, quantity: Int, variant: Variant?, toppings: List<Topping>) {
        if (item.status == "out_of_stock") {
            viewModelScope.launch {
                _toastMessage.emit("Món ăn hiện đã hết hàng.")
            }
            return
        }
        viewModelScope.launch {
            cartRepository.addToCart(
                CartItem(
                    id = item.id,
                    name = item.name,
                    image = item.image,
                    kitchenArea = item.kitchenArea,
                    quantity = quantity,
                    price = item.price,
                    status = item.status,
                    variant = variant,
                    toppings = toppings
                )
            )
        }
    }

    private fun fetchMenuItems() {
        viewModelScope.launch {
            foodRepository.getMenuItems(null).collect { resource ->
                _allMenuItems.value = when (resource) {
                    is Resource.Success -> UiState.Success(resource.data ?: emptyList())
                    is Resource.Error -> UiState.Error(resource.message ?: "Error")
                    is Resource.Loading -> UiState.Loading
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authPreference.clearAuth()
        }
    }
}
