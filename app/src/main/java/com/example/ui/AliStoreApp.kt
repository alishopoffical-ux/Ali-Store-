package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    object Home : Screen("home", "Store", Icons.Default.Home)
    object Info : Screen("info", "Connect", Icons.Default.Settings)
    object ProductDetails : Screen("product_details/{productId}", "Product Details") {
        fun createRoute(productId: Int) = "product_details/$productId"
    }
    object Reviews : Screen("reviews/{productId}", "Reviews") {
        fun createRoute(productId: Int) = "reviews/$productId"
    }
    object AdminLogin : Screen("admin_login", "Admin Login")
    object AdminDashboard : Screen("admin_dashboard", "Admin Dashboard")
}

@Composable
fun AliStoreApp(viewModel: StoreViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomNav = currentRoute == Screen.Home.route || currentRoute == Screen.Info.route

    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = showBottomNav) {
                NavigationBar {
                    listOf(Screen.Home, Screen.Info).forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel, onNavigateToProduct = { id ->
                    navController.navigate(Screen.ProductDetails.createRoute(id))
                })
            }
            composable(Screen.Info.route) {
                InfoScreen(viewModel, onNavigateToAdminLogin = {
                    navController.navigate(Screen.AdminLogin.route)
                })
            }
            composable(
                route = Screen.ProductDetails.route,
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
                ProductDetailsScreen(
                    productId = productId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToReviews = { id ->
                        navController.navigate(Screen.Reviews.createRoute(id))
                    }
                )
            }
            composable(
                route = Screen.Reviews.route,
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
                ReviewsScreen(
                    productId = productId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.AdminLogin.route) {
                AdminLoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.AdminLogin.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) // clear backstack
                        }
                    }
                )
            }
        }
    }
}
