package com.example.shopicaros.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.session.UserSessionRepository

class ProductDetailViewModelFactory(
    private val productRepository: ProductRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                ProductDetailViewModel::class.java
            )
        ) {

            return ProductDetailViewModel(
                productRepository,
                sessionRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}