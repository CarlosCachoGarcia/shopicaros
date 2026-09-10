package com.example.shopicaros.data.repository

import com.example.shopicaros.data.model.AuthResult
import com.example.shopicaros.data.model.LoginRequest
import com.example.shopicaros.data.remote.FakeStoreApi

class AuthRepositoryImpl(
    private val api: FakeStoreApi
) : AuthRepository {

    override suspend fun login(
        username: String,
        password: String
    ): AuthResult {

        val loginResponse =
            api.login(
                LoginRequest(
                    username = username,
                    password = password
                )
            )

        val users =
            api.getUsers()

        val user =
            users.firstOrNull {
                it.username.equals(
                    username,
                    ignoreCase = true
                )
            } ?: throw IllegalStateException(
                "No se encontró la información del usuario"
            )

        return AuthResult(
            token = loginResponse.token,
            userId = user.id,
            username = user.username
        )
    }
}