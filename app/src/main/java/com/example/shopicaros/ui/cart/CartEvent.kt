package com.example.shopicaros.ui.cart

sealed class CartEvent {

    data class ProductAdded(
        val quantity: Int
    ) : CartEvent()

    data class ShowMessage(
        val message: String
    ) : CartEvent()
}