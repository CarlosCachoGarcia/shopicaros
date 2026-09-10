package com.example.shopicaros.ui.users

import com.example.shopicaros.data.model.User

data class UsersUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)