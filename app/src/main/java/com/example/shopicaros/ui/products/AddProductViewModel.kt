package com.example.shopicaros.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.data.model.Product
import com.example.shopicaros.data.repository.ProductRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import java.net.URI
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val productRepository: ProductRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(AddProductUiState())

    val uiState: StateFlow<AddProductUiState> =
        _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<AddProductEvent>()

    val events: SharedFlow<AddProductEvent> =
        _events.asSharedFlow()

    fun saveProduct(
        title: String,
        priceText: String,
        description: String,
        category: String,
        imageUrl: String
    ) {

        if (
            sessionRepository.getRole() !=
            UserRole.ADMINISTRADOR
        ) {
            _uiState.update {
                it.copy(
                    generalError =
                        "Solo el Administrador puede agregar productos."
                )
            }
            return
        }

        val cleanTitle = title.trim()
        val cleanPrice = priceText.trim()
        val cleanDescription = description.trim()
        val cleanCategory = category.trim()
        val cleanImageUrl = imageUrl.trim()

        val price =
            cleanPrice.toDoubleOrNull()

        val titleError =
            if (cleanTitle.isBlank()) {
                "El título es obligatorio."
            } else {
                null
            }

        val priceError =
            when {
                cleanPrice.isBlank() ->
                    "El precio es obligatorio."

                price == null ->
                    "Ingresa un precio válido."

                price <= 0 ->
                    "El precio debe ser mayor a 0."

                else -> null
            }

        val descriptionError =
            if (cleanDescription.isBlank()) {
                "La descripción es obligatoria."
            } else {
                null
            }

        val categoryError =
            if (cleanCategory.isBlank()) {
                "La categoría es obligatoria."
            } else {
                null
            }

        val imageUrlError =
            when {
                cleanImageUrl.isBlank() ->
                    "La URL de imagen es obligatoria."

                !isValidHttpUrl(cleanImageUrl) ->
                    "Ingresa una URL válida."

                else -> null
            }

        _uiState.update {
            it.copy(
                titleError = titleError,
                priceError = priceError,
                descriptionError = descriptionError,
                categoryError = categoryError,
                imageUrlError = imageUrlError,
                generalError = null
            )
        }

        if (
            titleError != null ||
            priceError != null ||
            descriptionError != null ||
            categoryError != null ||
            imageUrlError != null
        ) {
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    generalError = null
                )
            }

            try {

                val product =
                    Product(
                        id = 0,
                        title = cleanTitle,
                        price = price!!,
                        description = cleanDescription,
                        category = cleanCategory,
                        image = cleanImageUrl
                    )

                val createdProduct =
                    productRepository.addProduct(
                        product
                    )

                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }

                _events.emit(
                    AddProductEvent.ProductCreated(
                        createdProduct
                    )
                )

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError =
                            "No se pudo registrar el producto."
                    )
                }
            }
        }
    }

    private fun isValidHttpUrl(
        value: String
    ): Boolean {

        return try {

            val uri = URI(value)

            (
                    uri.scheme == "http" ||
                            uri.scheme == "https"
                    ) &&
                    !uri.host.isNullOrBlank()

        } catch (e: Exception) {
            false
        }
    }
}