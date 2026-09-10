package com.example.shopicaros.data.model

data class AuthResult(
    val token: String,
    val userId: Int,
    val username: String
)