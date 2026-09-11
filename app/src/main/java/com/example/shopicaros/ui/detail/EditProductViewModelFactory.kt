package com.example.shopicaros.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.session.UserSessionRepository

class EditProductViewModelFactory(
    private val productRepository: ProductRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                EditProductViewModel::class.java
            )
        ) {

            return EditProductViewModel(
                productRepository,
                sessionRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}