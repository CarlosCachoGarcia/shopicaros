package com.example.shopicaros.ui.cart

import com.example.shopicaros.data.model.CartItem

data class CartUiState(
    val isAdding: Boolean = false,
    val isUpdating: Boolean = false,
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0
)