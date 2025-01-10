package com.example.login_app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.login_app.data.AuthRepository
import com.example.login_app.data.models.AuthResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {
    fun register(username: String, password: String, email: String, onResult: (AuthResponse?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.register(username, password, email)
                onResult(response, null)
            } catch (e: Exception) {
                onResult(null, e.message)
            }
        }
    }
}

class RegisterViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}