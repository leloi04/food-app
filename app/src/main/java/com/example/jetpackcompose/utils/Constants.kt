package com.example.jetpackcompose.utils

object Constants {
    const val TAG = "RestaurantApp"

    const val BASE_URL = "https://smart-menu-backend-r686.onrender.com"
    const val GOOGLE_WEB_CLIENT_ID = "YOUR_GOOGLE_WEB_CLIENT_ID.apps.googleusercontent.com" // Replace with actual ID

    const val STORE_LAT = 21.1167
    const val STORE_LNG = 105.9500

    fun getImageUrl(image: String, folder: String): String {
        return "$BASE_URL/images/$folder/$image"
    }
}
