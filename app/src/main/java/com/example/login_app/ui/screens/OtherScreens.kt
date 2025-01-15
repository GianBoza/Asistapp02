package com.example.login_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.login_app.ui.theme.WindowsXpBlue
import com.example.login_app.ui.theme.WindowsXpGrass
import com.example.login_app.ui.theme.WindowsXpSky

@Composable
fun ProfileScreen() {
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
                text = "Profile Screen",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = WindowsXpBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = { /* TODO: Add your action here */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WindowsXpBlue,
                    contentColor = Color.White
                )
            ) {
                Text("Edit Profile")
            }
        }
    }
}