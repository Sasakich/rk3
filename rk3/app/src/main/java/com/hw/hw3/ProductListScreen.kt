package com.hw.hw3

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hw.hw3.data.Product
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductListScreen(
    onNavigateToCart: () -> Unit,
    onProductClick: (Product) -> Unit,
    productViewModel: ProductViewModel = viewModel()
) {
    val uiState by productViewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    val products = remember { mutableStateListOf<Product>() }
    var isLoading by remember { mutableStateOf(false) }
    var hasMoreData by remember { mutableStateOf(true) }

    // Инициализация начальных данных
    LaunchedEffect(Unit) {
        if (uiState is UiState.Success) {
            val loadedProducts = (uiState as UiState.Success).products
            products.clear()
            isLoading = true
            for (i in 0 until loadedProducts.size) {
                delay(1000) // Задержка перед добавлением каждого товара
                products.add(loadedProducts[i]) // Добавляем один товар за раз
            }
            isLoading = false
        }
    }

    // Подгрузка товаров при прокрутке
    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
        val totalItemCount = lazyListState.layoutInfo.totalItemsCount
        // Если доходим до конца списка
        if (firstVisibleItemIndex >= totalItemCount - 5 && !isLoading && hasMoreData) {
            isLoading = true
            delay(1000) // Имитируем задержку
            val loadedProducts = (uiState as? UiState.Success)?.products
            if (loadedProducts != null) {
                // Добавляем новые товары по одному с задержкой
                for (i in products.size until (products.size + 10).coerceAtMost(loadedProducts.size)) {
                    delay(1000) // Задержка в 1 секунду для каждого товара
                    products.add(loadedProducts[i])
                }
                // Проверка, есть ли ещё товары
                if (products.size >= loadedProducts.size) {
                    hasMoreData = false
                }
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Средства для красоты") }, actions = {
                IconButton(onClick = onNavigateToCart) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Корзина")
                }
            })
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                itemsIndexed(products) { _, product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { onProductClick(product) }
                        ) {
                            Text(product.name, style = MaterialTheme.typography.h6)
                            Text("${product.price} ₽", style = MaterialTheme.typography.body1)
                            Text(product.description, style = MaterialTheme.typography.body2)
                        }
                    }
                }

                // Показываем индикатор загрузки, если загружаются новые товары
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                // Если больше нет данных для загрузки
                if (!hasMoreData) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Все товары загружены")
                        }
                    }
                }
            }
        }
    }
}
