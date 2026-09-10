package com.example.shopicaros.ui.login

sealed interface LoginEvent {

    data object LoginSuccess : LoginEvent
}