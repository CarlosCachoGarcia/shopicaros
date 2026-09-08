package com.example.shopicaros.data.repository

import com.example.shopicaros.data.model.Product
import com.example.shopicaros.data.remote.FakeStoreApi

class ProductRepositoryImpl(
    private val api: FakeStoreApi
) : ProductRepository {

    override suspend fun getProducts(): List<Product> {
        return api.getProducts()
    }

    override suspend fun getCategories(): List<String> {
        return api.getCategories()
    }

    override suspend fun getProductsByCategory(
        category: String
    ): List<Product> {
        return api.getProductsByCategory(category)
    }
}