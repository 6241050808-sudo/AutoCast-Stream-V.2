package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.service.AutoBrowserPresentationService
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep screen on while navigating or streaming in car
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Automatically start the car presentation service to monitor secondary displays
        try {
            AutoBrowserPresentationService.startService(this)
        } catch (_: Exception) {}

        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}
