package com.example.shopicaros.session

enum class UserRole(
    val value: String
) {

    ADMINISTRADOR("Administrador"),
    CLIENTE("Cliente"),
    AUDITOR("Auditor");

    companion object {

        fun fromValue(value: String?): UserRole {

            return entries.firstOrNull {
                it.value == value
            } ?: CLIENTE
        }
    }
}