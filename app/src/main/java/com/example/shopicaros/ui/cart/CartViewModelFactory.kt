package com.example.shopicaros.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopicaros.data.repository.CartRepository
import com.example.shopicaros.session.UserSessionRepository

class CartViewModelFactory(
    private val cartRepository: CartRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                CartViewModel::class.java
            )
        ) {

            return CartViewModel(
                cartRepository,
                sessionRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}