package com.example.shopicaros.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.data.model.Product
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            ProductDetailUiState()
        )

    val uiState: StateFlow<ProductDetailUiState> =
        _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<ProductDetailEvent>()

    val events: SharedFlow<ProductDetailEvent> =
        _events.asSharedFlow()

    fun loadProduct(
        productId: Int
    ) {

        if (productId <= 0) {

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage =
                        "Producto no disponible"
                )
            }

            return
        }

        if (
            _uiState.value.product?.id ==
            productId
        ) {
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
                    productRepository
                        .getProductById(
                            productId
                        )

                val role =
                    sessionRepository
                        .getRole()

                _uiState.update {
                    it.copy(
                        product = product,
                        isLoading = false,

                        canManageProduct =
                            role ==
                                    UserRole.ADMINISTRADOR,

                        canAddToCart =
                            role ==
                                    UserRole.CLIENTE
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

    fun applyUpdatedProduct(
        product: Product
    ) {

        _uiState.update {
            it.copy(
                product = product
            )
        }
    }

    fun deleteProduct() {

        val product =
            _uiState.value.product
                ?: return

        if (
            sessionRepository.getRole()
            != UserRole.ADMINISTRADOR
        ) {

            viewModelScope.launch {

                _events.emit(
                    ProductDetailEvent
                        .ShowMessage(
                            "No tienes permisos para eliminar productos."
                        )
                )
            }

            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isDeleting = true
                )
            }

            try {

                productRepository.deleteProduct(
                    product.id
                )

                _uiState.update {
                    it.copy(
                        product = null,
                        isDeleting = false
                    )
                }

                _events.emit(
                    ProductDetailEvent
                        .ProductDeleted(
                            product.id
                        )
                )

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isDeleting = false
                    )
                }

                _events.emit(
                    ProductDetailEvent
                        .ShowMessage(
                            "No fue posible eliminar el producto."
                        )
                )
            }
        }
    }
}