package com.example.shopicaros.session

import android.content.Context

class SharedPreferencesUserSessionRepository(
    context: Context
) : UserSessionRepository {

    private val preferences =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    override fun saveSession(
        token: String,
        role: UserRole
    ) {

        preferences
            .edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_ROLE, role.value)
            .apply()
    }

    override fun getToken(): String? {

        return preferences.getString(
            KEY_TOKEN,
            null
        )
    }

    override fun getRole(): UserRole {

        val storedRole =
            preferences.getString(
                KEY_ROLE,
                UserRole.CLIENTE.value
            )

        return UserRole.fromValue(
            storedRole
        )
    }

    override fun isLoggedIn(): Boolean {

        return !getToken().isNullOrBlank()
    }

    override fun clearSession() {

        preferences
            .edit()
            .clear()
            .apply()
    }

    companion object {

        private const val PREFS_NAME =
            "user_session"

        private const val KEY_TOKEN =
            "auth_token"

        private const val KEY_ROLE =
            "user_role"
    }
}