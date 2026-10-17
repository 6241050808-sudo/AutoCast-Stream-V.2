package com.example

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.service.AutoBrowserPresentationService
import com.example.ui.BrowserViewModel
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val browserViewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep screen on while navigating or streaming in car
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Automatically start the car presentation service to monitor secondary displays
        try {
            AutoBrowserPresentationService.startService(this)
        } catch (_: Exception) {}

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = browserViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val targetUrl = intent?.getStringExtra("NAVIGATE_URL")
        if (!targetUrl.isNullOrBlank()) {
            browserViewModel.navigateToUrl(targetUrl)
        }
    }
}
