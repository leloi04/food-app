package com.example.jetpackcompose.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackcompose.presentation.screen.login.LoginScreen
import com.example.jetpackcompose.presentation.screen.main.MainScreen
import com.example.jetpackcompose.presentation.screen.checkout.CheckoutScreen
import com.example.jetpackcompose.presentation.screen.orderdetail.OrderDetailScreen
import com.example.jetpackcompose.presentation.screen.ordersuccess.OrderSuccessScreen
import com.example.jetpackcompose.presentation.screen.register.RegisterScreen
import com.example.jetpackcompose.presentation.screen.splash.SplashScreen
import com.example.jetpackcompose.viewmodel.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { this.inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { this.inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { this.inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onNavigateToDetail = { menuId ->
                    navController.navigate(Screen.OrderDetail.createRoute(menuId))
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                }
            )
        }
        composable(Screen.Checkout.route) {
            val viewModel: CheckoutViewModel = hiltViewModel()
            CheckoutScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(navController.graph.startDestinationId) { this.inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("menuId") { type = NavType.StringType })
        ) {
            val viewModel: FoodDetailViewModel = hiltViewModel()
            OrderDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.OrderSuccess.route) {
            OrderSuccessScreen(
                onGoHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(navController.graph.startDestinationId) { this.inclusive = true }
                    }
                }
            )
        }
    }
}
