package com.example.shopicaros.data.repository

import com.example.shopicaros.data.model.Product

interface ProductRepository {

    suspend fun getProducts(): List<Product>

    suspend fun getCategories(): List<String>

    suspend fun getProductsByCategory(
        category: String
    ): List<Product>
}