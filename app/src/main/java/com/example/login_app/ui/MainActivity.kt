// MainActivity.kt
package com.example.login_app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.login_app.ui.login.LoginActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aquí puedes definir tu contenido principal
            // Por ejemplo, podrías iniciar LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}