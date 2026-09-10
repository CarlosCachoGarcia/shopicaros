package com.example.shopicaros.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.data.model.CartItem
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
        MutableStateFlow(
            CartUiState()
        )

    val uiState: StateFlow<CartUiState> =
        _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<CartEvent>()

    val events: SharedFlow<CartEvent> =
        _events.asSharedFlow()

    fun loadCart() {

        val items =
            cartRepository.getLocalItems()

        updateItems(
            items
        )
    }

    fun addProduct(
        product: Product,
        quantity: Int
    ) {

        if (
            sessionRepository.getRole() !=
            UserRole.CLIENTE
        ) {

            emitMessage(
                "No tienes permisos para agregar productos al carrito."
            )

            return
        }

        if (quantity <= 0) {

            emitMessage(
                "La cantidad debe ser mayor a cero."
            )

            return
        }

        val userId =
            sessionRepository.getUserId()

        if (userId <= 0) {

            emitMessage(
                "No se encontró una sesión válida."
            )

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

                val items =
                    cartRepository.getLocalItems()

                _uiState.update {
                    it.copy(
                        isAdding = false,
                        items = items,
                        total = calculateTotal(items)
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

    fun changeQuantity(
        productId: Int,
        newQuantity: Int
    ) {

        if (_uiState.value.isUpdating) {
            return
        }

        val userId =
            sessionRepository.getUserId()

        if (
            sessionRepository.getRole() !=
            UserRole.CLIENTE ||
            userId <= 0
        ) {

            emitMessage(
                "No tienes permisos para modificar el carrito."
            )

            return
        }

        if (newQuantity <= 0) {

            removeProduct(
                productId
            )

            return
        }

        val previousItems =
            _uiState.value.items

        val optimisticItems =
            previousItems.map { item ->

                if (
                    item.productId ==
                    productId
                ) {

                    item.copy(
                        quantity =
                            newQuantity
                    )

                } else {

                    item
                }
            }

        _uiState.update {
            it.copy(
                items = optimisticItems,
                total =
                    calculateTotal(
                        optimisticItems
                    ),
                isUpdating = true
            )
        }

        viewModelScope.launch {

            try {

                val updatedItems =
                    cartRepository
                        .updateProductQuantity(
                            userId = userId,
                            productId = productId,
                            quantity = newQuantity
                        )

                _uiState.update {
                    it.copy(
                        items = updatedItems,
                        total =
                            calculateTotal(
                                updatedItems
                            ),
                        isUpdating = false
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        items = previousItems,
                        total =
                            calculateTotal(
                                previousItems
                            ),
                        isUpdating = false
                    )
                }

                _events.emit(
                    CartEvent.ShowMessage(
                        "No fue posible actualizar la cantidad."
                    )
                )
            }
        }
    }

    fun removeProduct(
        productId: Int
    ) {

        if (_uiState.value.isUpdating) {
            return
        }

        val userId =
            sessionRepository.getUserId()

        if (
            sessionRepository.getRole() !=
            UserRole.CLIENTE ||
            userId <= 0
        ) {

            emitMessage(
                "No tienes permisos para modificar el carrito."
            )

            return
        }

        val previousItems =
            _uiState.value.items

        val optimisticItems =
            previousItems.filterNot {
                it.productId ==
                        productId
            }

        _uiState.update {
            it.copy(
                items = optimisticItems,
                total =
                    calculateTotal(
                        optimisticItems
                    ),
                isUpdating = true
            )
        }

        viewModelScope.launch {

            try {

                val updatedItems =
                    cartRepository.removeProduct(
                        userId = userId,
                        productId = productId
                    )

                _uiState.update {
                    it.copy(
                        items = updatedItems,
                        total =
                            calculateTotal(
                                updatedItems
                            ),
                        isUpdating = false
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        items = previousItems,
                        total =
                            calculateTotal(
                                previousItems
                            ),
                        isUpdating = false
                    )
                }

                _events.emit(
                    CartEvent.ShowMessage(
                        "No fue posible eliminar el producto del carrito."
                    )
                )
            }
        }
    }

    private fun updateItems(
        items: List<CartItem>
    ) {

        _uiState.update {
            it.copy(
                items = items,
                total =
                    calculateTotal(
                        items
                    )
            )
        }
    }

    private fun calculateTotal(
        items: List<CartItem>
    ): Double {

        return items.sumOf { item ->

            item.price *
                    item.quantity
        }
    }

    private fun emitMessage(
        message: String
    ) {

        viewModelScope.launch {

            _events.emit(
                CartEvent.ShowMessage(
                    message
                )
            )
        }
    }
}