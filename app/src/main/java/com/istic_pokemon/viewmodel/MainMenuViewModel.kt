package com.istic_pokemon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.istic_pokemon.repository.AuthRepository

class MainMenuViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    suspend fun getUsername(): String {
        return authRepository.getUsername() ?: "Usuario"
    }

    suspend fun logout() {
        authRepository.clearSession()
    }
}