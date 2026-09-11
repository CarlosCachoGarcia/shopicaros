package com.example.shopicaros.data.model

data class User(
    val id: Int,
    val email: String = "",
    val username: String,
    val name: UserName = UserName(),
    val phone: String = ""
) {

    val fullName: String
        get() {
            return listOf(
                name.firstname,
                name.lastname
            )
                .filter {
                    it.isNotBlank()
                }
                .joinToString(" ")
        }
}