package com.example.shopicaros.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.data.repository.AuthRepository
import com.example.shopicaros.session.UserRole
import com.example.shopicaros.session.UserSessionRepository
import java.io.IOException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<LoginEvent>()

    val events: SharedFlow<LoginEvent> =
        _events.asSharedFlow()

    fun onUsernameChanged(
        username: String
    ) {

        _uiState.update {
            it.copy(
                username = username,
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(
        password: String
    ) {

        _uiState.update {
            it.copy(
                password = password,
                errorMessage = null
            )
        }
    }

    fun login() {

        val username =
            _uiState.value.username.trim()

        val password =
            _uiState.value.password.trim()

        if (
            username.isBlank() ||
            password.isBlank()
        ) {

            _uiState.update {
                it.copy(
                    errorMessage =
                        "Ingresa tu usuario y contraseña."
                )
            }

            return
        }

        if (_uiState.value.isLoading) {
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

                val result =
                    authRepository.login(
                        username = username,
                        password = password
                    )

                val role =
                    UserRole.fromUserId(
                        result.userId
                    )
                sessionRepository.saveSession(
                    token = result.token,
                    userId = result.userId,
                    role = role
                )

                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }

                _events.emit(
                    LoginEvent.LoginSuccess
                )

            } catch (e: HttpException) {

                val message =
                    if (
                        e.code() == 401 ||
                        e.code() == 403
                    ) {

                        "Usuario o contraseña incorrectos."

                    } else {

                        "No fue posible iniciar sesión."
                    }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }

            } catch (e: IOException) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "No hay conexión con el servidor."
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "Ocurrió un error al iniciar sesión."
                    )
                }
            }
        }
    }
}