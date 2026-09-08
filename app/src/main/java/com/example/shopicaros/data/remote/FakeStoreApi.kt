package com.example.shopicaros.data.remote

import com.example.shopicaros.data.model.Product
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT

interface FakeStoreApi {

    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): List<Product>
    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): Product
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: Product
    ): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Product

}

