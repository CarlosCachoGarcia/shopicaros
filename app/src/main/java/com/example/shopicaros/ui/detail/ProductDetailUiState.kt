package com.example.shopicaros.ui.detail

import com.example.shopicaros.data.model.Product

data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val canManageProduct: Boolean = false,
    val canAddToCart: Boolean = false
)