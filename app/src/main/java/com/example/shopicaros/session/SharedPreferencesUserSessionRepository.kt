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
        userId: Int,
        role: UserRole
    ) {

        preferences
            .edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_ROLE, role.value)
            .apply()
    }

    override fun getToken(): String? {

        return preferences.getString(
            KEY_TOKEN,
            null
        )
    }

    override fun getUserId(): Int {

        return preferences.getInt(
            KEY_USER_ID,
            -1
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

        return !getToken().isNullOrBlank() &&
                getUserId() > 0
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

        private const val KEY_USER_ID =
            "user_id"

        private const val KEY_ROLE =
            "user_role"
    }
}