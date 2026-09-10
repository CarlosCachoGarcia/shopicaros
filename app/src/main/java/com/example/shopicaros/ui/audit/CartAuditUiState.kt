package com.example.shopicaros.ui.audit

import com.example.shopicaros.data.model.CartResponse

data class CartAuditUiState(
    val carts: List<CartResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)