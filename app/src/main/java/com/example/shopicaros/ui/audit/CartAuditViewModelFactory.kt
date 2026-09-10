package com.example.shopicaros.ui.audit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopicaros.data.repository.CartAuditRepository
import com.example.shopicaros.session.UserSessionRepository

class CartAuditViewModelFactory(
    private val repository: CartAuditRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                CartAuditViewModel::class.java
            )
        ) {

            return CartAuditViewModel(
                repository,
                sessionRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}