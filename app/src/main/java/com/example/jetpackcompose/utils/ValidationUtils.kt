package com.example.jetpackcompose.utils

import android.util.Patterns

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        val phoneRegex = "^(0[0-9]{9,10})$".toRegex()
        return phone.isNotEmpty() && phoneRegex.matches(phone)
    }

    fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$".toRegex()
        return password.isNotEmpty() && passwordRegex.matches(password)
    }

    fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.length >= 2
    }
}
