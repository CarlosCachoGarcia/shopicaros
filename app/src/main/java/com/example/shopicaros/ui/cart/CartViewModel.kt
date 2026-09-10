package com.example.shopicaros.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.data.model.Product
import com.example.shopicaros.data.repository.CartRepository
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

class CartViewModel(
    private val cartRepository: CartRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(CartUiState())

    val uiState: StateFlow<CartUiState> =
        _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<CartEvent>()

    val events: SharedFlow<CartEvent> =
        _events.asSharedFlow()

    fun addProduct(
        product: Product,
        quantity: Int
    ) {

        if (
            sessionRepository.getRole() !=
            UserRole.CLIENTE
        ) {

            viewModelScope.launch {
                _events.emit(
                    CartEvent.ShowMessage(
                        "No tienes permisos para agregar productos al carrito."
                    )
                )
            }

            return
        }

        if (quantity <= 0) {

            viewModelScope.launch {
                _events.emit(
                    CartEvent.ShowMessage(
                        "La cantidad debe ser mayor a cero."
                    )
                )
            }

            return
        }

        val userId =
            sessionRepository.getUserId()

        if (userId <= 0) {

            viewModelScope.launch {
                _events.emit(
                    CartEvent.ShowMessage(
                        "No se encontró una sesión válida."
                    )
                )
            }

            return
        }

        if (_uiState.value.isAdding) {
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isAdding = true
                )
            }

            try {

                val result =
                    cartRepository.addProductToCart(
                        userId = userId,
                        product = product,
                        quantity = quantity
                    )

                _uiState.update {
                    it.copy(
                        isAdding = false
                    )
                }

                _events.emit(
                    CartEvent.ProductAdded(
                        quantity = result.quantity
                    )
                )

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isAdding = false
                    )
                }

                _events.emit(
                    CartEvent.ShowMessage(
                        "No fue posible agregar el producto al carrito."
                    )
                )
            }
        }
    }
}