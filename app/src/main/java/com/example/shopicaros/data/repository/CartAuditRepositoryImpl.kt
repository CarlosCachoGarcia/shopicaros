package com.example.shopicaros.data.repository

import com.example.shopicaros.data.model.CartResponse
import com.example.shopicaros.data.remote.FakeStoreApi

class CartAuditRepositoryImpl(
    private val api: FakeStoreApi
) : CartAuditRepository {

    override suspend fun getCarts(): List<CartResponse> {

        return api.getCarts()
    }
}