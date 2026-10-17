package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BrowserViewModel
import com.example.ui.components.AutomotiveWebView
import com.example.ui.theme.AutoAmber
import com.example.ui.theme.AutoCyan
import com.example.ui.theme.AutoGreen
import com.example.ui.theme.AutoRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CarDisplaySimulatorScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var isMediaPlaying by remember { mutableStateOf(true) }
    var testUrlInput by remember { mutableStateOf(uiState.currentUrl) }
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header & Overview
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AutoCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = AutoCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column {
                Text(
                    text = "Android Auto Head Unit Simulator",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "จำลองและทบทวนการแสดงผลบนหน้าจอวิทยุรถยนต์จริง (Coolwalk UI)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Display Controls Bar (Ratio, Split-screen, Night mode)
        Card(
            modifier = Modifier.fillMaxWidth().testTag("car_simulator_controls_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ตัวเลือกการจำลองหน้าจอในรถ (Display Configuration)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AutoCyan
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Aspect Ratio: 16:9 vs 21:9
                    FilterChip(
                        selected = uiState.carSimulatorAspectRatio == "16:9",
                        onClick = { viewModel.setCarSimulatorAspectRatio("16:9") },
                        label = { Text("16:9 ปกติ", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.AspectRatio, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AutoCyan,
                            selectedLabelColor = Color(0xFF00363D)
                        ),
                        modifier = Modifier.testTag("ratio_16_9_chip")
                    )

                    FilterChip(
                        selected = uiState.carSimulatorAspectRatio == "21:9",
                        onClick = { viewModel.setCarSimulatorAspectRatio("21:9") },
                        label = { Text("21:9 จอกว้าง", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AutoCyan,
                            selectedLabelColor = Color(0xFF00363D)
                        ),
                        modifier = Modifier.testTag("ratio_21_9_chip")
                    )

                    // Coolwalk Split Mode Toggle
                    FilterChip(
                        selected = uiState.carSimulatorSplitMode,
                        onClick = { viewModel.setCarSimulatorSplitMode(!uiState.carSimulatorSplitMode) },
                        label = { Text(if (uiState.carSimulatorSplitMode) "โหมดแบ่งจอ" else "เต็มจอ", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.ViewSidebar, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AutoAmber,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.testTag("split_mode_chip")
                    )

                    // Night Anti-Glare Mode Toggle
                    IconButton(
                        onClick = { viewModel.setCarSimulatorNightMode(!uiState.carSimulatorNightMode) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .testTag("night_mode_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.carSimulatorNightMode) Icons.Default.Brightness4 else Icons.Default.Brightness7,
                            contentDescription = "Toggle Night/Day Mode",
                            tint = if (uiState.carSimulatorNightMode) AutoCyan else AutoAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // =========================================================================
        // REALISTIC ANDROID AUTO HEAD UNIT SIMULATOR (THE CAR SCREEN FRAME)
        // =========================================================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("in_car_head_unit_frame"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111622)),
            border = BorderStroke(3.dp, Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF475569))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Automotive Head Unit Brand & Status Bezel (Upper Rim)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AutoGreen)
                        )
                        Text(
                            text = "ANDROID AUTO • HEAD UNIT DISPLAY (SIMULATED)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PARKED (P) • 60 FPS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AutoGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                // Car Touchscreen Display Container
                val screenAspect = if (uiState.carSimulatorAspectRatio == "21:9") (21f / 10f) else (16f / 9.5f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(screenAspect)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (uiState.carSimulatorNightMode) Color(0xFF05070B) else Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // 1. Android Auto Coolwalk Status Rail (Left Bar)
                        Column(
                            modifier = Modifier
                                .width(52.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF0A0E17))
                                .border(BorderStroke(0.5.dp, Color(0xFF1E293B)))
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Upper group: App Grid Button + Clock
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 9-dot App Drawer Launcher button
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B))
                                        .clickable { },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Apps,
                                        contentDescription = "AA App Drawer",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Text(
                                    text = currentTime,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            // Middle group: App Switcher icons (AutoBrowser, Maps, Spotify, Phone)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // AutoBrowser (Active)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AutoCyan.copy(alpha = 0.25f))
                                        .border(1.5.dp, AutoCyan, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tv,
                                        contentDescription = "AutoBrowser",
                                        tint = AutoCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Google Maps
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                                        .clickable { viewModel.navigateToUrl("https://maps.google.com") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = "Maps",
                                        tint = Color(0xFF4ADE80),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Music Player
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                                        .clickable { viewModel.navigateToUrl("https://open.spotify.com") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = "Music",
                                        tint = Color(0xFF1DB954),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Phone
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E293B).copy(alpha = 0.7f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Phone",
                                        tint = Color(0xFF60A5FA),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Lower group: Assistant Mic + Temp + Signal
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Google Assistant",
                                    tint = AutoAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "5G",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }

                        // 2. Head Unit Screen Content (Full Browser OR Coolwalk Split Screen)
                        Row(modifier = Modifier.fillMaxSize()) {
                            // Main Browser Pane (takes 100% or 68% when split)
                            val browserWeight = if (uiState.carSimulatorSplitMode) 0.68f else 1f
                            Column(
                                modifier = Modifier
                                    .weight(browserWeight)
                                    .fillMaxHeight()
                            ) {
                                // Mini In-Car Browser Address & Control Bar
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF151C2C),
                                    tonalElevation = 4.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.injectAutofill("history.back()") },
                                            enabled = uiState.canGoBack,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = if (uiState.canGoBack) AutoCyan else Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.injectAutofill("history.forward()") },
                                            enabled = uiState.canGoForward,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = "Forward",
                                                tint = if (uiState.canGoForward) AutoCyan else Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.navigateToUrl(uiState.currentUrl) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Reload",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        // URL Address Pill inside Car Screen
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(28.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(Color(0xFF0F172A))
                                                .border(0.5.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                                                .padding(horizontal = 8.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Secure",
                                                    tint = AutoGreen,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Text(
                                                    text = uiState.currentUrl,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color(0xFFE2E8F0),
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }

                                        // Zoom scale badge on car screen
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AutoCyan.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${uiState.zoomScalePercent}%",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = AutoCyan,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }

                                // Interactive Embedded Web View on Car Screen!
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
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
                                        onVideoFullscreenChanged = { viewModel.setVideoFullscreen(it) }
                                    )
                                }
                            }

                            // Secondary Split-Screen Cards (Coolwalk Style)
                            if (uiState.carSimulatorSplitMode) {
                                Column(
                                    modifier = Modifier
                                        .weight(0.32f)
                                        .fillMaxHeight()
                                        .background(Color(0xFF0F172A))
                                        .border(BorderStroke(0.5.dp, Color(0xFF1E293B)))
                                        .padding(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Split Card 1: Android Auto Media Player Widget
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(AutoAmber),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                                }
                                                Column {
                                                    Text("In-Car Audio", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                                    Text("AutoBrowser Stream", color = Color.Gray, fontSize = 8.sp, maxLines = 1)
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.FastRewind, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                                IconButton(
                                                    onClick = { isMediaPlaying = !isMediaPlaying },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isMediaPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = AutoCyan,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Icon(Icons.Default.FastForward, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    // Split Card 2: In-Car Turn-by-Turn GPS Widget
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape)
                                                        .background(AutoGreen.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(imageVector = Icons.Default.Navigation, contentDescription = null, tint = AutoGreen, modifier = Modifier.size(16.dp))
                                                }
                                                Column {
                                                    Text("ใน 250 ม. เลี้ยวขวา", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    Text("ถนนสุขุมวิท (Sukhumvit)", color = Color.Gray, fontSize = 8.sp, maxLines = 1)
                                                }
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("15 นาที • 8.4 กม.", color = AutoCyan, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(3.dp))
                                                        .background(Color.Red)
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text("80", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // ROTARY DIAL & STEERING WHEEL CONTROLLER SIMULATION
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth().testTag("rotary_controller_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = AutoAmber)
                    Text(
                        text = "จำลองปุ่มหมุนคอนโซล & พวงมาลัย (Rotary Dial & Wheel Controls)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Text(
                    text = "ทดสอบการเลื่อนและควบคุมหน้าเว็บสำหรับรถยนต์ที่ใช้ปุ่ม Jog Dial (เช่น Mazda, BMW iDrive, Mercedes-Benz):",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.scrollWebPage(-350) },
                        modifier = Modifier.weight(1f).testTag("rotary_scroll_up_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = AutoCyan)
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("เลื่อนขึ้น", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.scrollWebPage(350) },
                        modifier = Modifier.weight(1f).testTag("rotary_scroll_down_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = AutoCyan)
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("เลื่อนลง", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.injectAutofill("document.activeElement ? document.activeElement.click() : null") },
                        modifier = Modifier.weight(1f).testTag("rotary_click_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AutoCyan, contentColor = Color(0xFF00363D))
                    ) {
                        Text("กดเลือก (Enter)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // =========================================================================
        // DISPLAY REVIEW & INSPECTION SUITE (ทบทวนและตรวจสอบการแสดงผลบนจอรถ)
        // =========================================================================
        Text(
            text = "การทบทวนและตรวจสอบคุณภาพการแสดงผล (In-Car Display Review)",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        // 1. Text Legibility & Zoom Rating
        Card(
            modifier = Modifier.fillMaxWidth().testTag("review_legibility_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = AutoGreen)
                    Text(
                        text = "1. การอ่านตัวอักษรจากระยะเบาะนั่ง (Legibility & Touch Target)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = "ระยะสายตาของคนขับห่างจากหน้าจอวิทยุประมาณ 60-80 ซม. ปรับขนาดเพื่อทดสอบความคมชัด:",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(100, 125, 150, 175).forEach { scale ->
                        FilterChip(
                            selected = uiState.zoomScalePercent == scale,
                            onClick = { viewModel.setZoomScale(scale) },
                            label = { Text("$scale%", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AutoCyan,
                                selectedLabelColor = Color(0xFF00363D)
                            ),
                            modifier = Modifier.testTag("preview_zoom_$scale")
                        )
                    }
                }
            }
        }

        // 2. Quick Car Web Testing Shortcuts
        Card(
            modifier = Modifier.fillMaxWidth().testTag("review_shortcuts_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Tv, contentDescription = null, tint = AutoCyan)
                    Text(
                        text = "2. ทดสอบเว็บไซต์ยอดนิยมบนจอรถยนต์ (Car-Optimized Web Test)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CarWebShortcutChip(
                        label = "YouTube",
                        icon = Icons.Default.PlayArrow,
                        onClick = { viewModel.navigateToUrl("https://www.youtube.com") },
                        modifier = Modifier.weight(1f)
                    )
                    CarWebShortcutChip(
                        label = "Google Maps",
                        icon = Icons.Default.Map,
                        onClick = { viewModel.navigateToUrl("https://maps.google.com") },
                        modifier = Modifier.weight(1f)
                    )
                    CarWebShortcutChip(
                        label = "Spotify",
                        icon = Icons.Default.MusicNote,
                        onClick = { viewModel.navigateToUrl("https://open.spotify.com") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CarWebShortcutChip(
                        label = "ปั๊มน้ำมัน",
                        icon = Icons.Default.LocalGasStation,
                        onClick = { viewModel.navigateToUrl("https://www.google.com/search?q=gas+station+near+me") },
                        modifier = Modifier.weight(1f)
                    )
                    CarWebShortcutChip(
                        label = "สถานีชาร์จ EV",
                        icon = Icons.Default.EvStation,
                        onClick = { viewModel.navigateToUrl("https://www.google.com/search?q=EV+charging+station+near+me") },
                        modifier = Modifier.weight(1f)
                    )
                    CarWebShortcutChip(
                        label = "สภาพอากาศ",
                        icon = Icons.Default.WbCloudy,
                        onClick = { viewModel.navigateToUrl("https://weather.com") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Real Car Hardware Status Review
        Card(
            modifier = Modifier.fillMaxWidth().testTag("review_hardware_status_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, if (uiState.isCarDisplayActive) AutoGreen else Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isCarDisplayActive) AutoGreen else AutoAmber)
                    )
                    Text(
                        text = if (uiState.isCarDisplayActive) "เชื่อมต่อหน้าจอรถยนต์จริงแล้ว (Active Car Screen)" else "พร้อมเชื่อมต่อหน้าจอ Android Auto (Standalone Engine)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isCarDisplayActive) AutoGreen else AutoAmber
                        )
                    )
                }
                Text(
                    text = "• ระบบประมวลผล: AutoBrowserPresentation (ฮาร์ดแวร์เร่งความเร็ว GPU 60fps)\n" +
                            "• ความละเอียดจอเสมือน: 1920x1080 Full HD (ปรับสเกลอัตโนมัติตามสัดส่วนจอรถ)\n" +
                            "• สตรีมมิ่งไร้สะดุด: ปลดล็อกเล่นวิดีโอและเสียงอัตโนมัติ 100% โดยไม่ต้องใช้ Fermata Auto",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CarWebShortcutChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, AutoCyan.copy(alpha = 0.3f)),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AutoCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold))
        }
    }
}
