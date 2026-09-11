package com.example.shopicaros.session

interface UserSessionRepository {

    fun saveSession(
        token: String,
        userId: Int,
        role: UserRole
    )

    fun getToken(): String?

    fun getUserId(): Int

    fun getRole(): UserRole

    fun isLoggedIn(): Boolean

    fun clearSession()
}