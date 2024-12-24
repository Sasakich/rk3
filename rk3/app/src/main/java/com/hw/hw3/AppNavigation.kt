package com.hw.hw3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.hw.hw3.data.Product
import java.net.URLDecoder

@Composable
fun AppNavigation(productViewModel: ProductViewModel = viewModel()) {
    val navController = rememberNavController()
    val cartItems by productViewModel.cartItems.collectAsState()

    NavHost(navController = navController, startDestination = "product_list") {
        composable("product_list") {
            ProductListScreen(
                onNavigateToCart = { navController.navigate("cart") },
                onProductClick = { product ->
                    val productJson = Gson().toJson(product)
                    navController.navigate("product_detail/$productJson")
                }
            )
        }
        composable("cart") {
            CartScreen(
                cartItems = cartItems,
                onBack = {  navController.popBackStack()  },
                onRemoveFromCart = { product -> productViewModel.removeFromCart(product) },
                onCheckout = {
                    paymentMethod ->
                    productViewModel.checkout(paymentMethod)
                }
            )

        }
        composable(
            route = "product_detail/{productJson}",
            arguments = listOf(navArgument("productJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val productJson = backStackEntry.arguments?.getString("productJson")
            val product: Product = Gson().fromJson(productJson, Product::class.java)

            ProductDetailScreen(
                product = product,
                onBack = { navController.popBackStack() },
                onAddToCart = { productViewModel.addToCart(it) }
            )
        }
    }
}
