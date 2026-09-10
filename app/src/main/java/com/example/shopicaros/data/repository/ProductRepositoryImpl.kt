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

    override suspend fun getProductById(
        id: Int
    ): Product {
        return api.getProductById(id)
    }
    override suspend fun addProduct(
        product: Product
    ): Product {
        return api.addProduct(product)
    }
    override suspend fun updateProduct(
        product: Product
    ): Product {
        return api.updateProduct(
            product.id,
            product
        )
    }

    override suspend fun deleteProduct(
        id: Int
    ): Product {
        return api.deleteProduct(id)
    }
}