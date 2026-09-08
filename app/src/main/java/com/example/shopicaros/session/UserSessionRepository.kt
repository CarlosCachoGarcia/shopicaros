package com.example.shopicaros.session

interface UserSessionRepository {

    fun getRole(): UserRole

    fun saveRole(role: UserRole)
}