package com.example.shopicaros.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopicaros.session.UserSessionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SessionViewModel(
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val _events =
        MutableSharedFlow<SessionEvent>()

    val events: SharedFlow<SessionEvent> =
        _events.asSharedFlow()

    fun logout() {

        sessionRepository.clearSession()

        viewModelScope.launch {

            _events.emit(
                SessionEvent.LoggedOut
            )
        }
    }
}