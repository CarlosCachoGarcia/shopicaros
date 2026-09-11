package com.example.shopicaros.ui.products

data class AddProductUiState(
    val isLoading: Boolean = false,
    val titleError: String? = null,
    val priceError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val imageUrlError: String? = null,
    val generalError: String? = null
)