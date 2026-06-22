package com.istic_pokemon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.istic_pokemon.repository.AuthRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    suspend fun checkSession() {
        val isLoggedIn = authRepository.isLoggedIn()
        if (isLoggedIn) {
            _loginState.postValue(LoginState.SessionActive)
        }
    }

    suspend fun login(username: String, password: String, rememberSession: Boolean) {
        _loginState.postValue(LoginState.Loading)

        //Credenciales por defecto (para demo)
        val validUsername = "admin"
        val validPassword = "admin123"

        if (username == validUsername && password == validPassword) {
            authRepository.saveUserSession(username, rememberSession)
            _loginState.postValue(LoginState.Success)
        } else {
            _loginState.postValue(LoginState.Error("Usuario o contraseña incorrectos"))
        }
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        object SessionActive : LoginState()
        data class Error(val message: String) : LoginState()
    }
}