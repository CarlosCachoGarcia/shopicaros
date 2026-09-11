package com.example.shopicaros.data.repository

import com.example.shopicaros.data.model.CartResponse

interface CartAuditRepository {

    suspend fun getCarts(): List<CartResponse>
}