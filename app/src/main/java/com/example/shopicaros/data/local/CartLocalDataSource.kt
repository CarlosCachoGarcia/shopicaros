package com.example.shopicaros.data.local

import com.example.shopicaros.data.model.CartItem

interface CartLocalDataSource {

    fun getItems(): List<CartItem>

    fun addOrUpdate(
        item: CartItem
    ): CartItem

    fun clear()
}