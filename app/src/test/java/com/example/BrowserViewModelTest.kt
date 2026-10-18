package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.BrowserTab
import com.example.ui.BrowserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BrowserViewModelTest {

    @Test
    fun testBrowserViewModelInitialization() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = BrowserViewModel(application)

        assertNotNull(viewModel.uiState.value)
        assertEquals("https://m.youtube.com", viewModel.uiState.value.currentUrl)
        assertEquals(BrowserTab.BROWSER, viewModel.uiState.value.activeTab)
        assertFalse(viewModel.uiState.value.isCarDisplayActive)
        assertEquals(125, viewModel.uiState.value.zoomScalePercent)
    }

    @Test
    fun testNavigationUrlFormatting() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = BrowserViewModel(application)

        viewModel.navigateToUrl("youtube.com")
        assertEquals("https://youtube.com", viewModel.uiState.value.currentUrl)

        viewModel.navigateToUrl("tesla news")
        assert(viewModel.uiState.value.currentUrl.startsWith("https://www.google.com/search?q="))
    }

    @Test
    fun testCarDisplaySimulatorControls() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = BrowserViewModel(application)

        // Switch to CAR_PREVIEW tab
        viewModel.setActiveTab(BrowserTab.CAR_PREVIEW)
        assertEquals(BrowserTab.CAR_PREVIEW, viewModel.uiState.value.activeTab)

        // Test Split Mode toggle
        assertFalse(viewModel.uiState.value.carSimulatorSplitMode)
        viewModel.setCarSimulatorSplitMode(true)
        assertEquals(true, viewModel.uiState.value.carSimulatorSplitMode)

        // Test Aspect Ratio setting
        viewModel.setCarSimulatorAspectRatio("21:9")
        assertEquals("21:9", viewModel.uiState.value.carSimulatorAspectRatio)

        // Test Rotary Scroll simulation
        viewModel.scrollWebPage(300)
        assertNotNull(viewModel.uiState.value.scriptToInject)
        assert(viewModel.uiState.value.scriptToInject?.contains("scrollBy") == true)
    }
}
