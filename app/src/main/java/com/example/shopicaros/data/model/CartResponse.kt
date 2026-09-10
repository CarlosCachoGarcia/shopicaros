package com.example.shopicaros.data.model

data class CartResponse(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProductRequest>
)