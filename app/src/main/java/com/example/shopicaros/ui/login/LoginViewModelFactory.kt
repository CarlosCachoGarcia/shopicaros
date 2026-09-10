package com.example.shopicaros.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopicaros.data.repository.AuthRepository
import com.example.shopicaros.session.UserSessionRepository

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                LoginViewModel::class.java
            )
        ) {

            return LoginViewModel(
                authRepository,
                sessionRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}