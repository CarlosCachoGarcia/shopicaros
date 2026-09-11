package com.example.shopicaros.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class EditProductViewModel(
    private val productRepository: ProductRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(EditProductUiState())

    val uiState: StateFlow<EditProductUiState> =
        _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<EditProductEvent>()

    val events: SharedFlow<EditProductEvent> =
        _events.asSharedFlow()

    fun loadProduct(productId: Int) {

        if (_uiState.value.product != null) {
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }

            try {

                val product =
                    productRepository
                        .getProductById(productId)

                _uiState.update {
                    it.copy(
                        product = product,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(isLoading = false)
                }

                _events.emit(
                    EditProductEvent.ShowMessage(
                        "No fue posible cargar el producto."
                    )
                )
            }
        }
    }

    fun saveChanges(
        title: String,
        priceText: String,
        category: String,
        description: String
    ) {

        val original =
            _uiState.value.product
                ?: return

        if (
            sessionRepository.getRole()
            != UserRole.ADMINISTRADOR
        ) {

            viewModelScope.launch {

                _events.emit(
                    EditProductEvent.ShowMessage(
                        "No tienes permisos para editar productos."
                    )
                )
            }

            return
        }

        val price =
            priceText.toDoubleOrNull()

        if (title.isBlank()) {
            emitValidationError(
                "El nombre es obligatorio."
            )
            return
        }

        if (price == null || price <= 0) {
            emitValidationError(
                "Ingresa un precio válido."
            )
            return
        }

        if (category.isBlank()) {
            emitValidationError(
                "La categoría es obligatoria."
            )
            return
        }

        if (description.isBlank()) {
            emitValidationError(
                "La descripción es obligatoria."
            )
            return
        }

        val updatedProduct =
            original.copy(
                title = title.trim(),
                price = price,
                category = category.trim(),
                description = description.trim()
            )

        viewModelScope.launch {

            _uiState.update {
                it.copy(isSaving = true)
            }

            try {

                val result =
                    productRepository
                        .updateProduct(
                            updatedProduct
                        )

                _uiState.update {
                    it.copy(
                        product = result,
                        isSaving = false
                    )
                }

                _events.emit(
                    EditProductEvent.Saved(
                        result
                    )
                )

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(isSaving = false)
                }

                _events.emit(
                    EditProductEvent.ShowMessage(
                        "No fue posible actualizar el producto."
                    )
                )
            }
        }
    }

    private fun emitValidationError(
        message: String
    ) {

        viewModelScope.launch {

            _events.emit(
                EditProductEvent.ShowMessage(
                    message
                )
            )
        }
    }
}