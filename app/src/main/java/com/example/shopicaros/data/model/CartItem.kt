package com.example.shopicaros.data.model

data class CartItem(
    val productId: Int,
    val title: String,
    val price: Double,
    val image: String,
    val quantity: Int
)