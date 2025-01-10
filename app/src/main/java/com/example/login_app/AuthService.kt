package com.example.login_app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
data class RegisterRequest(val username: String, val password: String, val email: String)
data class RegisterResponse(val message: String)
data class User(val id: Int, val username: String, val email: String)

interface AuthService {
    @POST("users/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("users/register/")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("users/")
    suspend fun getUsers(): List<User>
}

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    val api: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}