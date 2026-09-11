package com.example.shopicaros.data.repository

import com.example.shopicaros.data.model.User
import com.example.shopicaros.data.remote.FakeStoreApi

class UserRepositoryImpl(
    private val api: FakeStoreApi
) : UserRepository {

    override suspend fun getUsers(): List<User> {

        return api.getUsers()
    }
}