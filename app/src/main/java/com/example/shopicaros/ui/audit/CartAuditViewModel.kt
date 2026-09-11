package com.example.shopicaros.ui.audit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.data.repository.CartAuditRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartAuditViewModel(
    private val repository: CartAuditRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            CartAuditUiState()
        )

    val uiState: StateFlow<CartAuditUiState> =
        _uiState.asStateFlow()

    fun loadCarts() {

        val role =
            sessionRepository.getRole()

        if (
            role != UserRole.ADMINISTRADOR &&
            role != UserRole.AUDITOR
        ) {

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage =
                        "No tienes permisos para consultar el historial de carritos."
                )
            }

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

                val carts =
                    repository
                        .getCarts()
                        .sortedByDescending {
                            it.id
                        }

                _uiState.update {
                    it.copy(
                        carts = carts,
                        isLoading = false,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        carts = emptyList(),
                        isLoading = false,
                        errorMessage =
                            "No fue posible cargar el historial de carritos."
                    )
                }
            }
        }
    }

    fun retry() {

        loadCarts()
    }
}