package com.example.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.datastore.AuthPreference
import com.example.jetpackcompose.data.remote.dto.UpdatePasswordRequest
import com.example.jetpackcompose.data.remote.dto.UpdateUserRequest
import com.example.jetpackcompose.data.remote.dto.UserDto
import com.example.jetpackcompose.data.repository.AuthRepository
import com.example.jetpackcompose.utils.Resource
import com.example.jetpackcompose.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authPreference: AuthPreference,
    private val authRepository: AuthRepository
) : ViewModel() {

    val user = authPreference.userData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserDto(null, null, null, null, null, null, null)
    )

    private val _updateProfileState = MutableStateFlow<UiState<UserDto>>(UiState.Idle)
    val updateProfileState = _updateProfileState.asStateFlow()

    private val _updatePasswordState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updatePasswordState = _updatePasswordState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            authPreference.clearAuth()
        }
    }

    fun updateProfile(name: String, phone: String, gender: String, avatar: String) {
        val currentUser = user.value
        val userId = currentUser.id ?: return
        val email = currentUser.email ?: return

        viewModelScope.launch {
            val request = UpdateUserRequest(email, name, phone, gender, avatar)
            authRepository.updateUser(userId, request).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val updatedUser = resource.data!!
                        authPreference.saveUser(updatedUser)
                        _updateProfileState.value = UiState.Success(updatedUser)
                    }
                    is Resource.Error -> _updateProfileState.value = UiState.Error(resource.message ?: "Update failed")
                    is Resource.Loading -> _updateProfileState.value = UiState.Loading
                }
            }
        }
    }

    fun updatePassword(oldPass: String, newPass: String) {
        val email = user.value.email ?: return
        viewModelScope.launch {
            val request = UpdatePasswordRequest(email, oldPass, newPass)
            authRepository.updatePassword(request).collect { resource ->
                when (resource) {
                    is Resource.Success -> _updatePasswordState.value = UiState.Success(Unit)
                    is Resource.Error -> _updatePasswordState.value = UiState.Error(resource.message ?: "Failed")
                    is Resource.Loading -> _updatePasswordState.value = UiState.Loading
                }
            }
        }
    }

    fun resetStates() {
        _updateProfileState.value = UiState.Idle
        _updatePasswordState.value = UiState.Idle
    }
}
