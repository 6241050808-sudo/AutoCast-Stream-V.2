package com.example.presentation

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages detection and presentation on Android Auto / External Car Displays.
 * Automatically projects the browser onto the vehicle's infotainment screen.
 */
class CarDisplayManager(private val context: Context) : DisplayManager.DisplayListener {

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private var currentPresentation: AutoBrowserPresentation? = null

    private val _isCarDisplayConnected = MutableStateFlow(false)
    val isCarDisplayConnected: StateFlow<Boolean> = _isCarDisplayConnected.asStateFlow()

    private val _carDisplayInfo = MutableStateFlow<String?>(null)
    val carDisplayInfo: StateFlow<String?> = _carDisplayInfo.asStateFlow()

    fun startListening() {
        displayManager.registerDisplayListener(this, null)
        checkCurrentDisplays()
    }

    fun stopListening() {
        displayManager.unregisterDisplayListener(this)
        dismissPresentation()
    }

    private fun checkCurrentDisplays() {
        // Look for presentation category or any secondary display (Android Auto virtual display)
        val presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
        val allDisplays = displayManager.displays

        val carDisplay = presentationDisplays.firstOrNull()
            ?: allDisplays.firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }

        if (carDisplay != null) {
            setupPresentation(carDisplay)
        } else {
            dismissPresentation()
        }
    }

    private fun setupPresentation(display: Display) {
        if (currentPresentation?.display?.displayId == display.displayId) return

        dismissPresentation()
        try {
            val presentation = AutoBrowserPresentation(context, display)
            presentation.show()
            currentPresentation = presentation
            _isCarDisplayConnected.value = true
            _carDisplayInfo.value = "${display.name} (${display.width}x${display.height})"
        } catch (e: Exception) {
            _isCarDisplayConnected.value = false
            _carDisplayInfo.value = null
        }
    }

    private fun dismissPresentation() {
        currentPresentation?.dismiss()
        currentPresentation = null
        _isCarDisplayConnected.value = false
        _carDisplayInfo.value = null
    }

    fun loadUrl(url: String) {
        currentPresentation?.loadUrl(url)
    }

    fun injectScript(script: String) {
        currentPresentation?.evaluateJavascript(script)
    }

    fun setDesktopMode(isDesktop: Boolean) {
        currentPresentation?.isDesktopMode = isDesktop
    }

    fun setZoomScale(scale: Int) {
        currentPresentation?.zoomScalePercent = scale
    }

    fun goBack(): Boolean {
        return currentPresentation?.goBack() ?: false
    }

    fun goForward(): Boolean {
        return currentPresentation?.goForward() ?: false
    }

    fun reload() {
        currentPresentation?.reload()
    }

    override fun onDisplayAdded(displayId: Int) {
        val display = displayManager.getDisplay(displayId) ?: return
        if (display.displayId != Display.DEFAULT_DISPLAY) {
            setupPresentation(display)
        }
    }

    override fun onDisplayRemoved(displayId: Int) {
        if (currentPresentation?.display?.displayId == displayId) {
            dismissPresentation()
        }
    }

    override fun onDisplayChanged(displayId: Int) {
        if (currentPresentation?.display?.displayId == displayId) {
            // Display orientation / resolution changed (e.g. rotation in car)
            val display = displayManager.getDisplay(displayId)
            display?.let {
                _carDisplayInfo.value = "${it.name} (${it.width}x${it.height})"
            }
        }
    }
}
