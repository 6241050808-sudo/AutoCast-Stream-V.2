package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AutoAmber
import com.example.ui.theme.AutoCyan

@Composable
fun BrowserBottomBar(
    currentUrl: String,
    isLoading: Boolean,
    progress: Int,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isDesktopMode: Boolean,
    isFullScreen: Boolean,
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onToggleDesktop: () -> Unit,
    onToggleFullScreen: () -> Unit,
    onOpenAutofill: () -> Unit,
    onOpenBookmarks: () -> Unit,
    modifier: Modifier = Modifier
) {
    var urlText by remember { mutableStateOf(currentUrl) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(currentUrl) {
        urlText = currentUrl
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, AutoCyan.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // High Precision Web Loading Progress at the very top edge of the bottom bar
            if (isLoading) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = AutoCyan,
                    trackColor = Color.Transparent
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // In-Car Large Back Button
                IconButton(
                    onClick = onBackClick,
                    enabled = canGoBack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (canGoBack) AutoCyan else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }

                // Forward Button
                IconButton(
                    onClick = onForwardClick,
                    enabled = canGoForward,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_forward_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (canGoForward) AutoCyan else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }

                // Refresh Button
                IconButton(
                    onClick = onRefreshClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // In-car Touch-friendly URL Bar positioned at the bottom for easy reach
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("url_input_field"),
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AutoCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            keyboardController?.hide()
                            onNavigate(urlText)
                        }
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AutoCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (urlText.isNotEmpty()) {
                            IconButton(
                                onClick = { urlText = "" },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                )

                // Quick Autofill Fast Injection Button
                IconButton(
                    onClick = onOpenAutofill,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("open_autofill_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Quick Autofill",
                        tint = AutoAmber
                    )
                }

                // Desktop Mode Toggle
                IconButton(
                    onClick = onToggleDesktop,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("toggle_desktop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = "Desktop Mode",
                        tint = if (isDesktopMode) AutoCyan else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Bookmarks Quick Access
                IconButton(
                    onClick = onOpenBookmarks,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("quick_bookmarks_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Bookmarks",
                        tint = AutoAmber
                    )
                }

                // Fullscreen Theater Mode
                IconButton(
                    onClick = onToggleFullScreen,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("toggle_fullscreen_button")
                ) {
                    Icon(
                        imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Toggle Theater Mode",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Backward-compatible alias for BrowserBottomBar
 */
@Composable
fun BrowserTopBar(
    currentUrl: String,
    isLoading: Boolean,
    progress: Int,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isDesktopMode: Boolean,
    isFullScreen: Boolean,
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onToggleDesktop: () -> Unit,
    onToggleFullScreen: () -> Unit,
    onOpenAutofill: () -> Unit,
    onOpenBookmarks: () -> Unit,
    modifier: Modifier = Modifier
) {
    BrowserBottomBar(
        currentUrl = currentUrl,
        isLoading = isLoading,
        progress = progress,
        canGoBack = canGoBack,
        canGoForward = canGoForward,
        isDesktopMode = isDesktopMode,
        isFullScreen = isFullScreen,
        onNavigate = onNavigate,
        onBackClick = onBackClick,
        onForwardClick = onForwardClick,
        onRefreshClick = onRefreshClick,
        onToggleDesktop = onToggleDesktop,
        onToggleFullScreen = onToggleFullScreen,
        onOpenAutofill = onOpenAutofill,
        onOpenBookmarks = onOpenBookmarks,
        modifier = modifier
    )
}
