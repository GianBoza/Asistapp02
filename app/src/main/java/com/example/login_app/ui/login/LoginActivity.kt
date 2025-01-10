package com.example.login_app.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login_app.RetrofitInstance
import com.example.login_app.data.AuthRepository
import com.example.login_app.ui.home.HomeActivity
import com.example.login_app.ui.register.RegisterActivity

class LoginActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository(RetrofitInstance.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginContent(loginViewModel)
        }
    }
}

@Composable
fun LoginContent(viewModel: LoginViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.login(username, password) { response, error ->
                    if (response != null) {
                        Toast.makeText(context, "Login successful", Toast.LENGTH_LONG).show()
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                        (context as? ComponentActivity)?.finish()
                    } else {
                        Toast.makeText(context, "Login failed: $error", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = {
                val intent = Intent(context, RegisterActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account? Register here")
        }
    }
}