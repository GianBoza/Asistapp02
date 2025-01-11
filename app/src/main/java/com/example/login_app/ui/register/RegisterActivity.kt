package com.example.login_app.ui.register

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.login_app.RetrofitInstance
import com.example.login_app.data.AuthRepository
import com.example.login_app.ui.theme.LoginAppTheme
import com.example.login_app.ui.theme.WindowsXpBlue
import com.example.login_app.ui.theme.WindowsXpGrass
import com.example.login_app.ui.theme.WindowsXpSky

class RegisterActivity : ComponentActivity() {

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(AuthRepository(RetrofitInstance.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginAppTheme {
                RegisterScreen(registerViewModel)
            }
        }
    }
}

@Composable
fun RegisterScreen(viewModel: RegisterViewModel) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(WindowsXpSky, WindowsXpGrass)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Register", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))
                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        viewModel.register(username, password, email) { response, error ->
                            if (response != null) {
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show()
                                // Navegar de vuelta a LoginActivity
                                (context as? ComponentActivity)?.finish()
                            } else {
                                Toast.makeText(context, "Registration failed: $error", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Register")
                }
            }
        }
    }
}