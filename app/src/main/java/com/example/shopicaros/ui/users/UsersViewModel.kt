package com.example.shopicaros.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.data.repository.UserRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsersViewModel(
    private val userRepository: UserRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            UsersUiState()
        )

    val uiState: StateFlow<UsersUiState> =
        _uiState.asStateFlow()

    fun loadUsers() {

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
                        "No tienes permisos para consultar usuarios."
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

                val users =
                    userRepository.getUsers()

                _uiState.update {
                    it.copy(
                        users = users,
                        isLoading = false,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        users = emptyList(),
                        isLoading = false,
                        errorMessage =
                            "No fue posible cargar los usuarios."
                    )
                }
            }
        }
    }

    fun retry() {

        loadUsers()
    }
}