package com.example.shopicaros.ui.products

import com.example.shopicaros.data.model.Product

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = ALL_CATEGORY,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        const val ALL_CATEGORY = "Todos"
    }
}