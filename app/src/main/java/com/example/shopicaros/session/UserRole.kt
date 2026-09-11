package com.example.shopicaros.session

enum class UserRole(
    val value: String
) {

    ADMINISTRADOR("Administrador"),
    CLIENTE("Cliente"),
    AUDITOR("Auditor");

    companion object {

        fun fromValue(
            value: String?
        ): UserRole {

            return entries.firstOrNull {
                it.value == value
            } ?: CLIENTE
        }

        fun fromUserId(
            userId: Int
        ): UserRole {

            return when (userId) {

                1, 2 ->
                    ADMINISTRADOR

                3 ->
                    AUDITOR

                else ->
                    CLIENTE
            }
        }
    }
}