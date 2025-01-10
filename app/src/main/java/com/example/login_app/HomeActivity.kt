package com.example.login_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeContent()
        }
    }
}

@Composable
fun HomeContent() {
    // Aquí puedes definir el contenido de la pantalla principal
    Text(text = "Bienvenido a la pantalla principal")
}