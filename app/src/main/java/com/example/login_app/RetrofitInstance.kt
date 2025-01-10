package com.example.login_app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.login_app.data.AuthService

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