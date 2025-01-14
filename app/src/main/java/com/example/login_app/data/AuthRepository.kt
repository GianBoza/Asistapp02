package com.example.login_app.data

import com.example.login_app.data.models.AuthRequest
import com.example.login_app.data.models.AuthResponse
import retrofit2.HttpException

class AuthRepository(private val api: AuthService) {

    suspend fun login(username: String, password: String): AuthResponse {
        return try {
            api.login(AuthRequest(username, password))
        } catch (e: HttpException) {
            // Manejar errores HTTP específicos aquí
            throw Exception("Login failed: ${e.message()}")
        } catch (e: Exception) {
            // Manejar otros errores aquí
            throw Exception("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun register(username: String, password: String, email: String): AuthResponse {
        return try {
            api.register(AuthRequest(username, password, email))
        } catch (e: HttpException) {
            // Manejar errores HTTP específicos aquí
            throw Exception("Register failed: ${e.message()}")
        } catch (e: Exception) {
            // Manejar otros errores aquí
            throw Exception("An unexpected error occurred: ${e.message}")
        }
    }
}