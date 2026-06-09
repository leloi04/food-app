package com.example.jetpackcompose.viewmodel

import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.datastore.AuthPreference
import com.example.jetpackcompose.data.remote.dto.*
import com.example.jetpackcompose.data.repository.CartRepository
import com.example.jetpackcompose.data.repository.FoodRepository
import com.example.jetpackcompose.utils.Constants
import com.example.jetpackcompose.utils.Resource
import com.example.jetpackcompose.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val foodRepository: FoodRepository,
    private val authPreference: AuthPreference,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val cartItems = cartRepository.getCartItems().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _method = MutableStateFlow(DeliveryMethod.SHIP)
    val method = _method.asStateFlow()

    private val _payment = MutableStateFlow(PaymentMethod.COD)
    val payment = _payment.asStateFlow()

    private val _address = MutableStateFlow("")
    val address = _address.asStateFlow()

    private val _pickupTime = MutableStateFlow("")
    val pickupTime = _pickupTime.asStateFlow()

    private val _note = MutableStateFlow("")
    val note = _note.asStateFlow()

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm = _distanceKm.asStateFlow()

    private val _shipFee = MutableStateFlow(0L)
    val shipFee = _shipFee.asStateFlow()

    private val _checkoutState = MutableStateFlow<UiState<Any>>(UiState.Idle)
    val checkoutState = _checkoutState.asStateFlow()

    val totalItemPrice = cartItems.map { items ->
        items.sumOf { it.getTotalPrice() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalPayment = combine(totalItemPrice, shipFee, _method) { items, ship, m ->
        if (m == DeliveryMethod.SHIP) items + ship else items
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun setMethod(m: DeliveryMethod) { _method.value = m }
    fun setPayment(p: PaymentMethod) { _payment.value = p }
    fun setAddress(a: String) { 
        _address.value = a
        calculateDistance(a)
    }
    fun setPickupTime(t: String) { _pickupTime.value = t }
    fun setNote(n: String) { _note.value = n }

    private fun calculateDistance(address: String) {
        if (address.isEmpty()) {
            _distanceKm.value = 0.0
            _shipFee.value = 0
            return
        }

        viewModelScope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(address, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val location = addresses[0]
                    val destLat = location.latitude
                    val destLng = location.longitude
                    
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        Constants.STORE_LAT, Constants.STORE_LNG,
                        destLat, destLng,
                        results
                    )
                    
                    val distKm = results[0] / 1000.0
                    _distanceKm.value = Math.round(distKm * 10) / 10.0
                    _shipFee.value = (_distanceKm.value * 2000).toLong()
                }
            } catch (e: Exception) {
                // Fallback or ignore
            }
        }
    }

    fun placeOrder() {
        if (cartItems.value.isEmpty()) return
        
        if (_method.value == DeliveryMethod.SHIP && _address.value.isEmpty()) return
        if (_method.value == DeliveryMethod.PICKUP && _pickupTime.value.isEmpty()) return

        viewModelScope.launch {
            val customerId = authPreference.getUserId().first() ?: ""
            
            val request = CreateOrderRequest(
                customerId = customerId,
                method = _method.value.name.lowercase(),
                payment = _payment.value.name.lowercase(),
                deliveryAddress = if (_method.value == DeliveryMethod.SHIP) _address.value else null,
                pickupTime = if (_method.value == DeliveryMethod.PICKUP) _pickupTime.value else null,
                note = _note.value,
                totalItemPrice = totalItemPrice.value,
                totalPayment = totalPayment.value,
                orderItems = cartItems.value.map { item ->
                    OrderItemDto(
                        kitchenArea = item.kitchenArea,
                        menuItemId = item.id,
                        name = item.name,
                        quantity = item.quantity,
                        price = item.variant?.price ?: item.price,
                        variant = item.variant?.let { v ->
                            OrderVariantDto(v.id, v.label, v.price)
                        },
                        toppings = item.toppings.map { t ->
                            OrderToppingDto(t.id, t.name, t.price)
                        }
                    )
                }
            )

            foodRepository.createPreOrder(request).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _checkoutState.value = UiState.Loading
                    is Resource.Success -> {
                        _checkoutState.value = UiState.Success(resource.data!!)
                        cartRepository.clearCart()
                    }
                    is Resource.Error -> {
                        _checkoutState.value = UiState.Error(resource.message ?: "Order failed")
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun resetState() {
        _checkoutState.value = UiState.Idle
    }
}
