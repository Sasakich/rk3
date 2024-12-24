package com.hw.hw3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hw.hw3.data.MockData
import com.hw.hw3.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Модель состояния UI
sealed class UiState {
    object Loading : UiState()
    data class Success(val products: List<Product>) : UiState()
    data class Error(val message: String) : UiState()
    object Empty : UiState()
}

// Модель корзины
data class CartItem(
    val product: Product,
    var quantity: Int
)

class ProductViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                val products = MockData.getProducts()
                if (products.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    _uiState.value = UiState.Success(products)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка загрузки данных")
            }
        }
    }

    fun addToCart(product: Product) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentCart.add(CartItem(product, 1))
        }
        _cartItems.value = currentCart
    }

    fun removeFromCart(product: Product) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == product.id }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
            } else {
                currentCart.remove(existingItem)
            }
        }
        _cartItems.value = currentCart
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun checkout(paymentMethod: String) {
        println("Оплата выполнена. Способ оплаты: $paymentMethod")
        clearCart()
    }


}
