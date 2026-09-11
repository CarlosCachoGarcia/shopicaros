package com.example.shopicaros.data.local

import com.example.shopicaros.data.model.CartItem

interface CartLocalDataSource {

    fun getItems(): List<CartItem>

    fun addOrUpdate(
        item: CartItem
    ): CartItem

    fun updateQuantity(
        productId: Int,
        quantity: Int
    ): List<CartItem>

    fun removeItem(
        productId: Int
    ): List<CartItem>

    fun saveCartId(
        cartId: Int
    )

    fun getCartId(): Int?

    fun clear()
}