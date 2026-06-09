package com.example.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.remote.dto.RegisterRequest
import com.example.jetpackcompose.data.repository.AuthRepository
import com.example.jetpackcompose.utils.Resource
import com.example.jetpackcompose.utils.UiState
import com.example.jetpackcompose.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val registerState = _registerState.asStateFlow()

    val isNameValid = _name.map { ValidationUtils.isValidName(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isEmailValid = _email.map { ValidationUtils.isValidEmail(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isPhoneValid = _phone.map { ValidationUtils.isValidPhone(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isPasswordValid = _password.map { ValidationUtils.isValidPassword(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isConfirmPasswordValid = combine(_password, _confirmPassword) { p, cp ->
        cp.isNotEmpty() && p == cp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun onNameChange(v: String) { _name.value = v }
    fun onEmailChange(v: String) { _email.value = v }
    fun onPhoneChange(v: String) { _phone.value = v }
    fun onPasswordChange(v: String) { _password.value = v }
    fun onConfirmPasswordChange(v: String) { _confirmPassword.value = v }

    fun register() {
        if (!ValidationUtils.isValidName(_name.value) ||
            !ValidationUtils.isValidEmail(_email.value) ||
            !ValidationUtils.isValidPhone(_phone.value) ||
            !ValidationUtils.isValidPassword(_password.value) ||
            _password.value != _confirmPassword.value
        ) return

        viewModelScope.launch {
            val request = RegisterRequest(_name.value, _email.value, _password.value, _phone.value)
            repository.register(request).collect { resource ->
                when (resource) {
                    is Resource.Success -> _registerState.value = UiState.Success(Unit)
                    is Resource.Error -> _registerState.value = UiState.Error(resource.message ?: "Error")
                    is Resource.Loading -> _registerState.value = UiState.Loading
                }
            }
        }
    }

    fun resetState() { _registerState.value = UiState.Idle }
}
