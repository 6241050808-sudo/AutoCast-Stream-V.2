package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

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

    // Synchronize Desktop Mode user agent
    LaunchedEffect(isDesktopMode) {
        webViewRef?.let { wv ->
            wv.settings.userAgentString = if (isDesktopMode) {
                DESKTOP_USER_AGENT
            } else {
                null // Default mobile user agent
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
        webViewRef?.let { wv ->
            if (wv.url != url && !url.isBlank()) {
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
                        mediaPlaybackRequiresUserGesture = false // Smooth in-car video playback without tap restrictions
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        textZoom = zoomScalePercent
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        cacheMode = WebSettings.LOAD_DEFAULT
                        allowContentAccess = true
                        allowFileAccess = true
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val nextUrl = request?.url?.toString() ?: return false
                            if (nextUrl.startsWith("http://") || nextUrl.startsWith("https://")) {
                                return false
                            }
                            return true
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            url?.let { onPageStarted(it) }
                            onNavigationStateChanged(canGoBack(), canGoForward())
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            url?.let { onPageFinished(it, view?.title) }
                            onNavigationStateChanged(canGoBack(), canGoForward())
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
                    loadUrl(url)
                }
            },
            update = { wv ->
                webViewRef = wv
            }
        )

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
