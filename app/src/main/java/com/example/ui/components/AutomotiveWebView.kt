package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.AutoAmber
import com.example.ui.theme.AutoCyan

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AutomotiveWebView(
    url: String,
    isDesktopMode: Boolean,
    zoomScalePercent: Int,
    scriptToInject: String?,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String, String?) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onNavigationStateChanged: (Boolean, Boolean) -> Unit,
    onScriptInjected: () -> Unit,
    onVideoFullscreenChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var customVideoView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    var pageLoadError by remember { mutableStateOf<String?>(null) }
    var defaultSystemUserAgent by remember { mutableStateOf<String?>(null) }

    // Synchronize Desktop Mode user agent
    LaunchedEffect(isDesktopMode, webViewRef) {
        webViewRef?.let { wv ->
            if (defaultSystemUserAgent == null) {
                defaultSystemUserAgent = wv.settings.userAgentString
            }
            wv.settings.userAgentString = if (isDesktopMode) {
                DESKTOP_USER_AGENT
            } else {
                defaultSystemUserAgent
            }
            wv.reload()
        }
    }

    // Synchronize Display Zoom for Car Screen
    LaunchedEffect(zoomScalePercent) {
        webViewRef?.settings?.textZoom = zoomScalePercent
    }

    // Handle Script Injection for Form Autofill
    LaunchedEffect(scriptToInject) {
        if (!scriptToInject.isNullOrBlank()) {
            webViewRef?.evaluateJavascript(scriptToInject, null)
            onScriptInjected()
        }
    }

    // Synchronize URL navigation
    LaunchedEffect(url) {
        pageLoadError = null
        webViewRef?.let { wv ->
            if (wv.url != url && url.isNotBlank()) {
                wv.loadUrl(url)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.apply {
                stopLoading()
                clearHistory()
                removeAllViews()
                destroy()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)

                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        javaScriptCanOpenWindowsAutomatically = true
                        mediaPlaybackRequiresUserGesture = false // Unrestricted autoplay for in-car video/audio
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        textZoom = zoomScalePercent
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_DEFAULT
                        allowContentAccess = true
                        allowFileAccess = true
                        setSupportMultipleWindows(false)
                        defaultSystemUserAgent = userAgentString
                        if (isDesktopMode) {
                            userAgentString = DESKTOP_USER_AGENT
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val nextUrl = request?.url?.toString() ?: return false

                            // Allow standard web links
                            if (nextUrl.startsWith("http://") || nextUrl.startsWith("https://")) {
                                return false
                            }

                            // Handle Android intent URLs (e.g. YouTube app links) without breaking web streaming
                            if (nextUrl.startsWith("intent:") || nextUrl.startsWith("vnd.youtube:")) {
                                try {
                                    val parsedIntent = android.content.Intent.parseUri(
                                        nextUrl,
                                        android.content.Intent.URI_INTENT_SCHEME
                                    )
                                    val fallbackUrl = parsedIntent.getStringExtra("browser_fallback_url")
                                    if (!fallbackUrl.isNullOrBlank()) {
                                        view?.loadUrl(fallbackUrl)
                                        return true
                                    }

                                    // If vnd.youtube: extract video ID
                                    if (nextUrl.startsWith("vnd.youtube:")) {
                                        val videoId = nextUrl.substringAfter("vnd.youtube:").substringBefore("?")
                                        if (videoId.isNotBlank()) {
                                            view?.loadUrl("https://m.youtube.com/watch?v=$videoId")
                                            return true
                                        }
                                    }
                                } catch (_: Exception) {}
                                return true
                            }
                            return true
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            pageLoadError = null
                            url?.let { onPageStarted(it) }
                            onNavigationStateChanged(canGoBack(), canGoForward())
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            url?.let { onPageFinished(it, view?.title) }
                            onNavigationStateChanged(canGoBack(), canGoForward())
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            if (request?.isForMainFrame == true) {
                                pageLoadError = error?.description?.toString() ?: "ไม่สามารถเชื่อมต่ออินเทอร์เน็ตได้"
                            }
                        }

                        @SuppressLint("WebViewClientOnReceivedSslError")
                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            // On vehicle infotainment displays, proceed through benign internal/proxy cert warnings
                            handler?.proceed()
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            onProgressChanged(newProgress)
                            onNavigationStateChanged(canGoBack(), canGoForward())
                        }

                        override fun onShowCustomView(
                            view: View?,
                            callback: CustomViewCallback?
                        ) {
                            if (customVideoView != null) {
                                callback?.onCustomViewHidden()
                                return
                            }
                            customVideoView = view
                            customViewCallback = callback
                            onVideoFullscreenChanged(true)
                        }

                        override fun onHideCustomView() {
                            customVideoView = null
                            customViewCallback?.onCustomViewHidden()
                            customViewCallback = null
                            onVideoFullscreenChanged(false)
                        }
                    }

                    webViewRef = this
                    val target = if (url.isBlank()) "https://m.youtube.com" else url
                    loadUrl(target)
                }
            },
            update = { wv ->
                webViewRef = wv
            }
        )

        // Error & Recovery Card if page fails to load
        pageLoadError?.let { err ->
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .testTag("webview_error_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AutoAmber)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AutoAmber,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "ไม่สามารถเปิดหน้าเว็บได้ชั่วคราว",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "สาเหตุ: $err\nกรุณาตรวจสอบการเชื่อมต่ออินเทอร์เน็ตของรถ หรือกดปุ่มด้านล่างเพื่อเปิด YouTube ใหม่",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            lineHeight = 18.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                pageLoadError = null
                                webViewRef?.loadUrl("https://m.youtube.com")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AutoCyan, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("เปิด YouTube", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                pageLoadError = null
                                webViewRef?.reload()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ลองใหม่", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Custom Fullscreen View for HTML5 Videos (YouTube, streaming media)
        customVideoView?.let { videoView ->
            AndroidView(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                factory = {
                    FrameLayout(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        addView(
                            videoView,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                }
            )
        }
    }
}
