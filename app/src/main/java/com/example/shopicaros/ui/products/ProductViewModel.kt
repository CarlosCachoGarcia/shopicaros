package com.example.shopicaros.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())

    val uiState: StateFlow<ProductUiState> =
        _uiState.asStateFlow()

    private var filterJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    products = emptyList(),
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {

                val categoriesRequest = async {
                    repository.getCategories()
                }

                val productsRequest = async {
                    repository.getProducts()
                }

                val categories = categoriesRequest.await()
                val products = productsRequest.await()

                _uiState.update {
                    it.copy(
                        categories = categories,
                        products = products,
                        selectedCategory = ProductUiState.ALL_CATEGORY,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudieron cargar los productos."
                    )
                }
            }
        }
    }

    fun selectCategory(category: String) {

        filterJob?.cancel()

        filterJob = viewModelScope.launch {

            _uiState.update {
                it.copy(
                    selectedCategory = category,

                    // Requisito US04:
                    // eliminar datos anteriores durante la nueva petición.
                    products = emptyList(),

                    isLoading = true,
                    errorMessage = null
                )
            }

            try {

                val products =
                    if (category == ProductUiState.ALL_CATEGORY) {

                        repository.getProducts()

                    } else {

                        repository.getProductsByCategory(category)
                    }

                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudieron cargar los productos."
                    )
                }
            }
        }
    }

    fun retry() {

        if (_uiState.value.categories.isEmpty()) {
            loadInitialData()
        } else {
            selectCategory(_uiState.value.selectedCategory)
        }
    }
}