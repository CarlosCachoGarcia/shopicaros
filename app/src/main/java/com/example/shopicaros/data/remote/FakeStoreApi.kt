package com.example.shopicaros.data.remote

import com.example.shopicaros.data.model.LoginRequest
import com.example.shopicaros.data.model.LoginResponse
import com.example.shopicaros.data.model.Product
import com.example.shopicaros.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.example.shopicaros.data.model.CartRequest
import com.example.shopicaros.data.model.CartResponse
interface FakeStoreApi {

    // -------------------------
    // AUTENTICACIÓN
    // -------------------------

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("users")
    suspend fun getUsers(): List<User>


    // -------------------------
    // PRODUCTOS
    // -------------------------

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
    @POST("products")
    suspend fun addProduct(
        @Body product: Product
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

    // -------------------------
// CARRITO
// -------------------------

    @POST("carts")
    suspend fun addCart(
        @Body request: CartRequest
    ): CartResponse
}