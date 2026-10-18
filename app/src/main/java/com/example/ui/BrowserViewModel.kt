package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AutoBrowserDatabase
import com.example.data.BrowserRepository
import com.example.data.model.AutofillSnippetEntity
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import com.example.presentation.CarDisplayManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class BrowserTab {
    BROWSER,
    CAR_PREVIEW,
    BOOKMARKS,
    AUTOFILL,
    KINGINSTALLER_GUIDE,
    SETTINGS
}

data class BrowserUiState(
    val currentUrl: String = "https://m.youtube.com",
    val pageTitle: String = "AutoBrowser",
    val loadingProgress: Int = 0,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isDesktopMode: Boolean = false,
    val zoomScalePercent: Int = 125, // Default 125% for car screens
    val isFullScreenMode: Boolean = false,
    val isVideoFullscreen: Boolean = false,
    val activeTab: BrowserTab = BrowserTab.BROWSER,
    val selectedBookmarkCategory: String = "All",
    val showAutofillQuickDrawer: Boolean = false,
    val showAddBookmarkDialog: Boolean = false,
    val showAddAutofillDialog: Boolean = false,
    val scriptToInject: String? = null,
    val nightModeHighContrast: Boolean = true,
    val isCarDisplayActive: Boolean = false,
    val carDisplayDetails: String? = null,
    val carSimulatorSplitMode: Boolean = false,
    val carSimulatorNightMode: Boolean = true,
    val carSimulatorAspectRatio: String = "16:9"
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BrowserRepository = BrowserRepository(
        AutoBrowserDatabase.getDatabase(application).browserDao()
    )
    val carDisplayManager: CarDisplayManager = CarDisplayManager(application)

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.bookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val autofillSnippets: StateFlow<List<AutofillSnippetEntity>> = repository.autofillSnippets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentHistory: StateFlow<List<HistoryEntity>> = repository.recentHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        carDisplayManager.startListening()

        viewModelScope.launch {
            carDisplayManager.isCarDisplayConnected.collect { connected ->
                _uiState.value = _uiState.value.copy(isCarDisplayActive = connected)
            }
        }

        viewModelScope.launch {
            carDisplayManager.carDisplayInfo.collect { details ->
                _uiState.value = _uiState.value.copy(carDisplayDetails = details)
            }
        }
    }

    fun navigateToUrl(inputUrl: String) {
        val trimmed = inputUrl.trim()
        val destination = when {
            trimmed.isBlank() -> "https://www.google.com"
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=" + java.net.URLEncoder.encode(trimmed, "UTF-8")
        }
        _uiState.value = _uiState.value.copy(
            currentUrl = destination,
            activeTab = BrowserTab.BROWSER,
            showAutofillQuickDrawer = false
        )
        // Also stream directly to the connected car display
        carDisplayManager.loadUrl(destination)
    }

    fun onPageStarted(url: String) {
        _uiState.value = _uiState.value.copy(
            currentUrl = url,
            isLoading = true,
            loadingProgress = 10
        )
    }

    fun onPageFinished(url: String, title: String?) {
        val cleanTitle = title ?: url
        _uiState.value = _uiState.value.copy(
            currentUrl = url,
            pageTitle = cleanTitle,
            isLoading = false,
            loadingProgress = 100
        )
        viewModelScope.launch {
            repository.addHistory(cleanTitle, url)
        }
    }

    fun onProgressChanged(progress: Int) {
        _uiState.value = _uiState.value.copy(
            loadingProgress = progress,
            isLoading = progress < 100
        )
    }

    fun updateNavigationState(canBack: Boolean, canForward: Boolean) {
        _uiState.value = _uiState.value.copy(
            canGoBack = canBack,
            canGoForward = canForward
        )
    }

    fun toggleDesktopMode() {
        val nextMode = !_uiState.value.isDesktopMode
        _uiState.value = _uiState.value.copy(
            isDesktopMode = nextMode
        )
        carDisplayManager.setDesktopMode(nextMode)
    }

    fun setZoomScale(scalePercent: Int) {
        val clamped = scalePercent.coerceIn(80, 200)
        _uiState.value = _uiState.value.copy(
            zoomScalePercent = clamped
        )
        carDisplayManager.setZoomScale(clamped)
    }

    fun toggleFullScreenMode() {
        _uiState.value = _uiState.value.copy(
            isFullScreenMode = !_uiState.value.isFullScreenMode
        )
    }

    fun setVideoFullscreen(isFullscreen: Boolean) {
        _uiState.value = _uiState.value.copy(
            isVideoFullscreen = isFullscreen
        )
    }

    fun setActiveTab(tab: BrowserTab) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun toggleAutofillQuickDrawer() {
        _uiState.value = _uiState.value.copy(
            showAutofillQuickDrawer = !_uiState.value.showAutofillQuickDrawer
        )
    }

    fun setBookmarkCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedBookmarkCategory = category)
    }

    fun setAddBookmarkDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddBookmarkDialog = show)
    }

    fun setAddAutofillDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddAutofillDialog = show)
    }

    fun saveBookmark(title: String, url: String, category: String) {
        viewModelScope.launch {
            repository.addBookmark(title, url, category)
            setAddBookmarkDialog(false)
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
        }
    }

    fun saveAutofillSnippet(label: String, value: String, category: String) {
        viewModelScope.launch {
            repository.addAutofillSnippet(label, value, category)
            setAddAutofillDialog(false)
        }
    }

    fun deleteAutofillSnippet(snippet: AutofillSnippetEntity) {
        viewModelScope.launch {
            repository.deleteAutofillSnippet(snippet)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun resetDefaultBookmarks() {
        viewModelScope.launch {
            repository.resetDefaults()
        }
    }

    /**
     * Injects value into active focused input or first suitable input field in the page
     */
    fun injectAutofill(value: String) {
        val sanitized = value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
        val script = """
            (function() {
                var el = document.activeElement;
                if (!el || (el.tagName !== 'INPUT' && el.tagName !== 'TEXTAREA')) {
                    el = document.querySelector('input[type="text"], input[type="search"], input:not([type]), textarea');
                }
                if (el) {
                    el.focus();
                    el.value = "$sanitized";
                    el.dispatchEvent(new Event('input', { bubbles: true }));
                    el.dispatchEvent(new Event('change', { bubbles: true }));
                }
            })();
        """.trimIndent()

        _uiState.value = _uiState.value.copy(
            scriptToInject = script,
            showAutofillQuickDrawer = false,
            activeTab = BrowserTab.BROWSER
        )
        // Also inject into the car presentation screen
        carDisplayManager.injectScript(script)
    }

    fun onScriptInjected() {
        _uiState.value = _uiState.value.copy(scriptToInject = null)
    }

    fun setCarSimulatorSplitMode(split: Boolean) {
        _uiState.value = _uiState.value.copy(carSimulatorSplitMode = split)
    }

    fun setCarSimulatorNightMode(night: Boolean) {
        _uiState.value = _uiState.value.copy(carSimulatorNightMode = night)
    }

    fun setCarSimulatorAspectRatio(ratio: String) {
        _uiState.value = _uiState.value.copy(carSimulatorAspectRatio = ratio)
    }

    /**
     * Simulates scrolling the active web page using in-car steering wheel or rotary jog dial.
     */
    fun scrollWebPage(deltaY: Int) {
        val script = "window.scrollBy({ top: $deltaY, behavior: 'smooth' });"
        _uiState.value = _uiState.value.copy(scriptToInject = script)
        carDisplayManager.injectScript(script)
    }

    override fun onCleared() {
        carDisplayManager.stopListening()
        super.onCleared()
    }
}
