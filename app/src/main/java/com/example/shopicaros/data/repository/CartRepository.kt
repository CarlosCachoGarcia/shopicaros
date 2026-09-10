package com.example.shopicaros.data.repository

import com.example.shopicaros.data.model.CartItem
import com.example.shopicaros.data.model.Product

interface CartRepository {

    suspend fun addProductToCart(
        userId: Int,
        product: Product,
        quantity: Int
    ): CartItem

    fun getLocalItems(): List<CartItem>

    fun clearLocalCart()
}