package com.example.shopicaros.ui.products

import com.example.shopicaros.data.model.Product

sealed interface AddProductEvent {

    data class ProductCreated(
        val product: Product
    ) : AddProductEvent
}