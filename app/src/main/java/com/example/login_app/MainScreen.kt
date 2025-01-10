package com.example.login_app

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.*

@Composable
fun MainScreen() {
    var showLogin by remember { mutableStateOf(true) }

    if (showLogin) {
        LoginContent(onRegisterClick = { showLogin = false })
    } else {
        RegisterContent(onLoginClick = { showLogin = true })
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}