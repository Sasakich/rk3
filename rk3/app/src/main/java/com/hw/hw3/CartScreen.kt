package com.hw.hw3

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hw.hw3.data.Product

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onBack: () -> Unit,
    onRemoveFromCart: (Product) -> Unit,
    onCheckout: (String) -> Unit
) {
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Корзина") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Корзина пуста")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(cartItems) { cartItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(cartItem.product.name, style = MaterialTheme.typography.body1)
                                Text("Цена: ${cartItem.product.price} ₽", style = MaterialTheme.typography.body2)
                                Text("Количество: ${cartItem.quantity}", style = MaterialTheme.typography.body2)
                            }
                            IconButton(onClick = { onRemoveFromCart(cartItem.product) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showPaymentDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Оплатить")
                }
            }
        }

        // Диалог выбора способа оплаты
        if (showPaymentDialog) {
            AlertDialog(
                onDismissRequest = { showPaymentDialog = false },
                title = { Text("Выберите способ оплаты") },
                text = { Text("Выберите один из доступных способов оплаты:") },
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedPaymentMethod = "Карта"
                                showPaymentDialog = false
                                showSuccessDialog = true
                                onCheckout("Карта")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Карта")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                selectedPaymentMethod = "Наличные"
                                showPaymentDialog = false
                                showSuccessDialog = true
                                onCheckout("Наличные")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Наличные")
                        }
                    }
                }
            )
        }

        // Диалог подтверждения успешной оплаты
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Оплата прошла успешно") },
                text = { Text("Вы выбрали способ оплаты: $selectedPaymentMethod") },
                buttons = {
                    Button(
                        onClick = { showSuccessDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Ок")
                    }
                }
            )
        }
    }
}
