package com.example.presentation

import android.annotation.SuppressLint
import android.app.Presentation
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

/**
 * Custom Presentation that directly renders on the Android Auto car display (Secondary / Virtual Display).
 * This eliminates the dependency on third-party mirroring tools like Fermata Auto.
 */
class AutoBrowserPresentation(
    outerContext: Context,
    display: Display
) : Presentation(outerContext, display) {

    private var rootLayout: FrameLayout? = null
    private var webView: WebView? = null
    private var customVideoView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    var isDesktopMode: Boolean = false
        set(value) {
            field = value
            webView?.let { wv ->
                wv.settings.userAgentString = if (value) DESKTOP_USER_AGENT else null
                wv.reload()
            }
        }

    var zoomScalePercent: Int = 125
        set(value) {
            field = value
            webView?.settings?.textZoom = value
        }

    var onPageStatusChanged: ((url: String, title: String?, isLoading: Boolean, progress: Int) -> Unit)? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep car display alive and hardware accelerated
        window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            setBackgroundDrawableResource(android.R.color.black)
        }

        val layout = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.BLACK)
        }
        rootLayout = layout
        setContentView(layout)

        setupCarWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupCarWebView() {
        val wv = WebView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            setBackgroundColor(Color.BLACK)

            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false // Unrestricted in-car audio & video
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
                userAgentString = if (isDesktopMode) DESKTOP_USER_AGENT else null
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
                    url?.let { onPageStatusChanged?.invoke(it, view?.title, true, 15) }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let { onPageStatusChanged?.invoke(it, view?.title, false, 100) }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    view?.url?.let { onPageStatusChanged?.invoke(it, view.title, newProgress < 100, newProgress) }
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

                    // Hide webview and show fullscreen video player on car screen
                    webView?.visibility = View.GONE
                    rootLayout?.addView(
                        view,
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    )
                }

                override fun onHideCustomView() {
                    customVideoView?.let { videoView ->
                        rootLayout?.removeView(videoView)
                    }
                    customVideoView = null
                    customViewCallback?.onCustomViewHidden()
                    customViewCallback = null
                    webView?.visibility = View.VISIBLE
                }
            }
        }

        webView = wv
        rootLayout?.addView(wv)
        wv.loadUrl("https://www.youtube.com")
    }

    fun loadUrl(url: String) {
        webView?.loadUrl(url)
    }

    fun evaluateJavascript(script: String) {
        webView?.evaluateJavascript(script, null)
    }

    fun goBack(): Boolean {
        if (customVideoView != null) {
            webView?.webChromeClient?.onHideCustomView()
            return true
        }
        if (webView?.canGoBack() == true) {
            webView?.goBack()
            return true
        }
        return false
    }

    fun goForward(): Boolean {
        if (webView?.canGoForward() == true) {
            webView?.goForward()
            return true
        }
        return false
    }

    fun reload() {
        webView?.reload()
    }

    override fun onDetachedFromWindow() {
        webView?.apply {
            stopLoading()
            clearHistory()
            removeAllViews()
            destroy()
        }
        webView = null
        super.onDetachedFromWindow()
    }
}
