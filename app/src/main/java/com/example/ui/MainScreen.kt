package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddAutofillDialog
import com.example.ui.components.AddBookmarkDialog
import com.example.ui.components.AutomotiveWebView
import com.example.ui.components.BrowserBottomBar
import com.example.ui.components.QuickAutofillDrawer
import com.example.ui.screens.AutofillScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.CarDisplaySimulatorScreen
import com.example.ui.screens.KingInstallerGuideScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AutoAmber
import com.example.ui.theme.AutoCyan
import com.example.ui.theme.AutoGreen

@Composable
fun MainScreen(
    viewModel: BrowserViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val autofillSnippets by viewModel.autofillSnippets.collectAsStateWithLifecycle()

    // Predictive back handler hierarchy
    BackHandler(
        enabled = uiState.isVideoFullscreen ||
                uiState.isFullScreenMode ||
                uiState.showAutofillQuickDrawer ||
                uiState.activeTab != BrowserTab.BROWSER
    ) {
        when {
            uiState.isVideoFullscreen -> viewModel.setVideoFullscreen(false)
            uiState.isFullScreenMode -> viewModel.toggleFullScreenMode()
            uiState.showAutofillQuickDrawer -> viewModel.toggleAutofillQuickDrawer()
            uiState.activeTab != BrowserTab.BROWSER -> viewModel.setActiveTab(BrowserTab.BROWSER)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            // Note: The main browser toolbar has been moved to the bottom for driver reachability!
            // When an external Android Auto car display is connected, a sleek floating indicator is displayed.
            if (uiState.isCarDisplayActive && !uiState.isFullScreenMode) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = Color(0xFF0F172A).copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AutoGreen),
                    tonalElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AutoGreen)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Android Auto Car Screen Active • ${uiState.carDisplayDetails ?: "Secondary Display"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AutoGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (!uiState.isFullScreenMode) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Quick Autofill Drawer appearing directly above the bottom toolbar
                    AnimatedVisibility(
                        visible = uiState.showAutofillQuickDrawer,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        QuickAutofillDrawer(
                            snippets = autofillSnippets,
                            onSnippetClick = { snippetValue ->
                                viewModel.injectAutofill(snippetValue)
                            },
                            onAddNewClick = { viewModel.setAddAutofillDialog(true) },
                            onCloseClick = { viewModel.toggleAutofillQuickDrawer() }
                        )
                    }

                    // BOTTOM BROWSER TOOLBAR (Moved from top to bottom as requested)
                    if (uiState.activeTab == BrowserTab.BROWSER) {
                        BrowserBottomBar(
                            currentUrl = uiState.currentUrl,
                            isLoading = uiState.isLoading,
                            progress = uiState.loadingProgress,
                            canGoBack = uiState.canGoBack,
                            canGoForward = uiState.canGoForward,
                            isDesktopMode = uiState.isDesktopMode,
                            isFullScreen = uiState.isFullScreenMode,
                            onNavigate = { viewModel.navigateToUrl(it) },
                            onBackClick = {
                                if (uiState.canGoBack) {
                                    viewModel.injectAutofill("history.back()")
                                }
                            },
                            onForwardClick = {
                                if (uiState.canGoForward) {
                                    viewModel.injectAutofill("history.forward()")
                                }
                            },
                            onRefreshClick = { viewModel.navigateToUrl(uiState.currentUrl) },
                            onToggleDesktop = { viewModel.toggleDesktopMode() },
                            onToggleFullScreen = { viewModel.toggleFullScreenMode() },
                            onOpenAutofill = { viewModel.toggleAutofillQuickDrawer() },
                            onOpenBookmarks = { viewModel.setActiveTab(BrowserTab.BOOKMARKS) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Bottom Navigation with M3 standards & navigationBars insets
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = uiState.activeTab == BrowserTab.BROWSER,
                            onClick = { viewModel.setActiveTab(BrowserTab.BROWSER) },
                            icon = {
                                Icon(imageVector = Icons.Default.Language, contentDescription = "Browser")
                            },
                            label = { Text("Browser", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AutoCyan,
                                selectedTextColor = AutoCyan,
                                indicatorColor = AutoCyan.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_browser")
                        )

                        // NEW: Android Auto Screen Simulator & Review Tab
                        NavigationBarItem(
                            selected = uiState.activeTab == BrowserTab.CAR_PREVIEW,
                            onClick = { viewModel.setActiveTab(BrowserTab.CAR_PREVIEW) },
                            icon = {
                                Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = "AA Display")
                            },
                            label = { Text("AA Display", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AutoCyan,
                                selectedTextColor = AutoCyan,
                                indicatorColor = AutoCyan.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_car_preview")
                        )

                        NavigationBarItem(
                            selected = uiState.activeTab == BrowserTab.BOOKMARKS,
                            onClick = { viewModel.setActiveTab(BrowserTab.BOOKMARKS) },
                            icon = {
                                Icon(imageVector = Icons.Default.Star, contentDescription = "Bookmarks")
                            },
                            label = { Text("Bookmarks", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AutoAmber,
                                selectedTextColor = AutoAmber,
                                indicatorColor = AutoAmber.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_bookmarks")
                        )

                        NavigationBarItem(
                            selected = uiState.activeTab == BrowserTab.AUTOFILL,
                            onClick = { viewModel.setActiveTab(BrowserTab.AUTOFILL) },
                            icon = {
                                Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Quick Fill")
                            },
                            label = { Text("Quick Fill", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AutoAmber,
                                selectedTextColor = AutoAmber,
                                indicatorColor = AutoAmber.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_autofill")
                        )

                        NavigationBarItem(
                            selected = uiState.activeTab == BrowserTab.KINGINSTALLER_GUIDE,
                            onClick = { viewModel.setActiveTab(BrowserTab.KINGINSTALLER_GUIDE) },
                            icon = {
                                Icon(imageVector = Icons.Default.Book, contentDescription = "AA Guide")
                            },
                            label = { Text("AA Guide", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AutoCyan,
                                selectedTextColor = AutoCyan,
                                indicatorColor = AutoCyan.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_guide")
                        )

                        NavigationBarItem(
                            selected = uiState.activeTab == BrowserTab.SETTINGS,
                            onClick = { viewModel.setActiveTab(BrowserTab.SETTINGS) },
                            icon = {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                            },
                            label = { Text("Settings", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AutoCyan,
                                selectedTextColor = AutoCyan,
                                indicatorColor = AutoCyan.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_settings")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                BrowserTab.BROWSER -> {
                    AutomotiveWebView(
                        url = uiState.currentUrl,
                        isDesktopMode = uiState.isDesktopMode,
                        zoomScalePercent = uiState.zoomScalePercent,
                        scriptToInject = uiState.scriptToInject,
                        onPageStarted = { viewModel.onPageStarted(it) },
                        onPageFinished = { url, title -> viewModel.onPageFinished(url, title) },
                        onProgressChanged = { viewModel.onProgressChanged(it) },
                        onNavigationStateChanged = { canBack, canForward ->
                            viewModel.updateNavigationState(canBack, canForward)
                        },
                        onScriptInjected = { viewModel.onScriptInjected() },
                        onVideoFullscreenChanged = { isFs -> viewModel.setVideoFullscreen(isFs) }
                    )

                    // Active Android Auto Car Display Streaming Indicator
                    if (uiState.isCarDisplayActive) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            color = Color(0xFF0F172A).copy(alpha = 0.92f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AutoGreen),
                            tonalElevation = 6.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AutoGreen)
                                )
                                Text(
                                    text = "Android Auto Car Screen Active • ${uiState.carDisplayDetails ?: "Secondary Display"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AutoGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Floating Theater Mode Exit Button (when in full screen)
                    if (uiState.isFullScreenMode) {
                        FloatingActionButton(
                            onClick = { viewModel.toggleFullScreenMode() },
                            containerColor = Color.Black.copy(alpha = 0.7f),
                            contentColor = AutoCyan,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(48.dp)
                                .testTag("exit_fullscreen_fab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FullscreenExit,
                                contentDescription = "Exit Theater Mode",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                BrowserTab.CAR_PREVIEW -> {
                    CarDisplaySimulatorScreen(
                        viewModel = viewModel
                    )
                }

                BrowserTab.BOOKMARKS -> {
                    BookmarksScreen(
                        bookmarks = bookmarks,
                        selectedCategory = uiState.selectedBookmarkCategory,
                        currentUrl = uiState.currentUrl,
                        onCategorySelect = { viewModel.setBookmarkCategory(it) },
                        onBookmarkClick = { url -> viewModel.navigateToUrl(url) },
                        onAddBookmarkClick = { viewModel.setAddBookmarkDialog(true) },
                        onDeleteBookmark = { viewModel.deleteBookmark(it) },
                        onResetDefaults = { viewModel.resetDefaultBookmarks() }
                    )
                }

                BrowserTab.AUTOFILL -> {
                    AutofillScreen(
                        snippets = autofillSnippets,
                        onInjectSnippet = { value -> viewModel.injectAutofill(value) },
                        onAddNewSnippet = { viewModel.setAddAutofillDialog(true) },
                        onDeleteSnippet = { viewModel.deleteAutofillSnippet(it) }
                    )
                }

                BrowserTab.KINGINSTALLER_GUIDE -> {
                    KingInstallerGuideScreen()
                }

                BrowserTab.SETTINGS -> {
                    SettingsScreen(
                        zoomScalePercent = uiState.zoomScalePercent,
                        isDesktopMode = uiState.isDesktopMode,
                        onZoomChange = { viewModel.setZoomScale(it) },
                        onToggleDesktop = { viewModel.toggleDesktopMode() },
                        onResetBookmarks = { viewModel.resetDefaultBookmarks() },
                        onClearHistory = { viewModel.clearHistory() }
                    )
                }
            }
        }
    }

    // Add Bookmark Dialog
    if (uiState.showAddBookmarkDialog) {
        AddBookmarkDialog(
            initialUrl = uiState.currentUrl,
            initialTitle = uiState.pageTitle,
            onDismiss = { viewModel.setAddBookmarkDialog(false) },
            onConfirm = { title, url, category ->
                viewModel.saveBookmark(title, url, category)
            }
        )
    }

    // Add Autofill Macro Dialog
    if (uiState.showAddAutofillDialog) {
        AddAutofillDialog(
            onDismiss = { viewModel.setAddAutofillDialog(false) },
            onConfirm = { label, value, category ->
                viewModel.saveAutofillSnippet(label, value, category)
            }
        )
    }
}
