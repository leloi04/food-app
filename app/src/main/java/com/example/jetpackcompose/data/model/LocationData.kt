package com.example.jetpackcompose.data.model

data class LocationData(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class LocationUiState {
    object Idle : LocationUiState()
    object RequestingPermission : LocationUiState()
    object CheckingGPS : LocationUiState()
    object GettingLocation : LocationUiState()
    object ConvertingAddress : LocationUiState()
    data class Success(val data: LocationData) : LocationUiState()
    data class Error(val message: String, val type: ErrorType = ErrorType.UNKNOWN) : LocationUiState()

    enum class ErrorType {
        PERMISSION_DENIED,
        GPS_DISABLED,
        TIMEOUT,
        GEOCODER_FAILED,
        UNKNOWN
    }
}
