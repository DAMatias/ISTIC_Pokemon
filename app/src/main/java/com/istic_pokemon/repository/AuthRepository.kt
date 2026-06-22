package com.istic_pokemon.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthRepository(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("auth")

    suspend fun saveUserSession(username: String, rememberSession: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
            preferences[SESSION_ACTIVE_KEY] = if (rememberSession) "true" else "false"
            if (rememberSession) {
                preferences[REMEMBER_SESSION_KEY] = "true"
            }
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[SESSION_ACTIVE_KEY] == "true"
        }.first()
    }

    suspend fun getUsername(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[USERNAME_KEY]
        }.first()
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(USERNAME_KEY)
            preferences.remove(SESSION_ACTIVE_KEY)
            preferences.remove(REMEMBER_SESSION_KEY)
        }
    }

    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val SESSION_ACTIVE_KEY = stringPreferencesKey("session_active")
        private val REMEMBER_SESSION_KEY = stringPreferencesKey("remember_session")
    }
}