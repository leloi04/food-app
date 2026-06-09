package com.example.jetpackcompose.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Home : Screen("home")
    object Menu : Screen("menu")
    object Cart : Screen("cart")
    object Account : Screen("account")
    object Orders : Screen("orders")
    object Checkout : Screen("checkout")
    object OrderSuccess : Screen("order_success")
    object OrderDetail : Screen("order_detail/{menuId}") {
        fun createRoute(menuId: String) = "order_detail/$menuId"
    }
}
