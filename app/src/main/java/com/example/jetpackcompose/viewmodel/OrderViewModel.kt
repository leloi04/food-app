package com.example.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import com.example.jetpackcompose.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor() : ViewModel() {
    private val _orderState = MutableStateFlow<UiState<String>>(UiState.Success("Order Screen Content"))
    val orderState = _orderState.asStateFlow()
}
