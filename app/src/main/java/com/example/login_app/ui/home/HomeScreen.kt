package com.example.login_app.ui.home

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.login_app.ui.login.LoginActivity
import com.example.login_app.ui.theme.WindowsXpBlue
import com.example.login_app.ui.theme.WindowsXpGrass
import com.example.login_app.ui.theme.WindowsXpSky

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current

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
            Text(
                text = "Welcome to the Home Screen",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = WindowsXpBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    navController.navigate("location_catch")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WindowsXpBlue,
                    contentColor = Color.White
                )
            ) {
                Text("Go to Location Catch Screen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Navegar a LoginActivity
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WindowsXpBlue,
                    contentColor = Color.White
                )
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}