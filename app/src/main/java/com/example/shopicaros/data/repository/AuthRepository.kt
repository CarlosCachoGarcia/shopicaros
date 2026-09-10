package com.example.shopicaros.data.repository

import com.example.shopicaros.data.model.AuthResult

interface AuthRepository {

    suspend fun login(
        username: String,
        password: String
    ): AuthResult
}