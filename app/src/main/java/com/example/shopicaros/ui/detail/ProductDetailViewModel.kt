package com.example.shopicaros.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ProductDetailUiState())

    val uiState: StateFlow<ProductDetailUiState> =
        _uiState.asStateFlow()

    fun loadProduct(productId: Int) {

        if (productId <= 0) {

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Producto no disponible"
                )
            }

            return
        }

        if (_uiState.value.product?.id == productId) {
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {

                val product =
                    productRepository.getProductById(
                        productId
                    )

                val role =
                    sessionRepository.getRole()

                _uiState.update {
                    it.copy(
                        product = product,
                        isLoading = false,
                        canManageProduct =
                            role == UserRole.ADMINISTRADOR
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        product = null,
                        isLoading = false,
                        errorMessage =
                            "Producto no disponible"
                    )
                }
            }
        }
    }
}