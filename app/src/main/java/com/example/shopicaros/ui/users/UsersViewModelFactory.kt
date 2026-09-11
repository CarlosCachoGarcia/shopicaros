package com.example.shopicaros.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopicaros.data.repository.UserRepository
import com.example.shopicaros.session.UserSessionRepository

class UsersViewModelFactory(
    private val userRepository: UserRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                UsersViewModel::class.java
            )
        ) {

            return UsersViewModel(
                userRepository,
                sessionRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}