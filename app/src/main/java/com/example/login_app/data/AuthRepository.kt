package com.example.login_app.data

import com.example.login_app.data.models.AuthRequest
import com.example.login_app.data.models.AuthResponse
import com.example.login_app.data.models.User

class AuthRepository(private val api: AuthService) {

    suspend fun login(username: String, password: String): AuthResponse {
        return api.login(AuthRequest(username, password))
    }

    suspend fun register(username: String, password: String, email: String): AuthResponse {
        return api.register(AuthRequest(username, password, email))
    }

    suspend fun getUsers(token: String): List<User> {
        return api.getUsers(token)
    }
}