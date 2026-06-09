package com.example.jetpackcompose.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.jetpackcompose.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem(Screen.Home, "Trang chủ", Icons.Default.Home),
        NavigationItem(Screen.Menu, "Thực đơn", Icons.Default.RestaurantMenu),
        NavigationItem(Screen.Cart, "Giỏ hàng", Icons.Default.ShoppingCart), // Center FAB
        NavigationItem(Screen.Account, "Tài khoản", Icons.Default.Person),
        NavigationItem(Screen.Orders, "Đơn hàng", Icons.AutoMirrored.Filled.ListAlt)
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        NavigationBar(
            modifier = Modifier.height(80.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            items.forEachIndexed { index, item ->
                if (index == 2) {
                    // Spacer for the FAB
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {},
                        enabled = false,
                        label = { Text("") }
                    )
                } else {
                    val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (selected) Color(0xFFFF9800) else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                item.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) Color(0xFFFF9800) else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }

        // Floating Cart FAB
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.Cart.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(60.dp),
            shape = CircleShape,
            containerColor = Color(0xFFFF9800),
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", modifier = Modifier.size(30.dp))
        }
    }
}

data class NavigationItem(val screen: Screen, val title: String, val icon: ImageVector)
