package com.example.shopicaros.ui.detail

sealed interface ProductDetailEvent {

    data class ProductDeleted(
        val productId: Int
    ) : ProductDetailEvent

    data class ShowMessage(
        val message: String
    ) : ProductDetailEvent
}