package com.example.login_app.data

import com.example.login_app.data.models.AuthRequest
import com.example.login_app.data.models.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("users/login/")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("users/register/")
    suspend fun register(@Body request: AuthRequest): AuthResponse
}