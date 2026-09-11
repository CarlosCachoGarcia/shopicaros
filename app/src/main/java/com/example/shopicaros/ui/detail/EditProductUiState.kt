package com.example.shopicaros.ui.detail

import com.example.shopicaros.data.model.Product

data class EditProductUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)