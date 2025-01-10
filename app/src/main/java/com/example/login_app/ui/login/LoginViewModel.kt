package com.example.login_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.login_app.data.AuthRepository
import com.example.login_app.data.models.AuthResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    fun login(username: String, password: String, onResult: (AuthResponse?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.login(username, password)
                onResult(response, null)
            } catch (e: Exception) {
                onResult(null, e.message)
            }
        }
    }
}

class LoginViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}