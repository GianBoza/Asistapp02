package com.example.login_app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.login_app.ui.home.HomeScreen
import com.example.login_app.ui.login.LoginScreen
import com.example.login_app.ui.register.RegisterScreen
import com.example.login_app.ui.screens.LocationCatchScreen
import com.example.login_app.ui.theme.LoginAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("home") { HomeScreen(navController) }
                    composable("location_catch") { LocationCatchScreen(this@MainActivity) }
                }
            }
        }
    }
}