package com.example.shopicaros.data.model

data class CartRequest(
    val userId: Int,
    val date: String,
    val products: List<CartProductRequest>
)