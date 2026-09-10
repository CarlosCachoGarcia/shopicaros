package com.example.shopicaros.session

interface UserSessionRepository {

    fun saveSession(
        token: String,
        role: UserRole
    )

    fun getToken(): String?

    fun getRole(): UserRole

    fun isLoggedIn(): Boolean

    fun clearSession()
}