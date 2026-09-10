package com.example.shopicaros.ui.session

sealed interface SessionEvent {

    data object LoggedOut : SessionEvent
}