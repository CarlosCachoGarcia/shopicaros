package com.example.shopicaros.data.repository

import com.example.shopicaros.data.model.User

interface UserRepository {

    suspend fun getUsers(): List<User>
}