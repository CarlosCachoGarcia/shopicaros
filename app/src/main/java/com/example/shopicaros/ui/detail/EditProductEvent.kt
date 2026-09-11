package com.example.shopicaros.ui.detail

import com.example.shopicaros.data.model.Product

sealed interface EditProductEvent {

    data class Saved(
        val product: Product
    ) : EditProductEvent

    data class ShowMessage(
        val message: String
    ) : EditProductEvent
}