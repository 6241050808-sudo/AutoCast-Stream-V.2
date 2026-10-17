package com.example.service

import android.app.PendingIntent
import android.content.Intent
import android.media.MediaDescription
import android.media.browse.MediaBrowser.MediaItem
import android.media.session.MediaSession
import android.os.Bundle
import android.service.media.MediaBrowserService
import com.example.MainActivity

/**
 * MediaBrowserService implementation for Android Auto discovery and Customize Launcher registration.
 * Android Auto queries MediaBrowserService to display apps in the launcher and provide media integration.
 */
class AutoBrowserMediaBrowserService : MediaBrowserService() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        // Automatically start the Car Presentation background service when Android Auto binds
        try {
            AutoBrowserPresentationService.startService(applicationContext)
        } catch (_: Exception) {}

        // Create and configure MediaSession
        mediaSession = MediaSession(this, "AutoBrowserMediaSession").apply {
            val intent = Intent(this@AutoBrowserMediaBrowserService, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                this@AutoBrowserMediaBrowserService,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            setSessionActivity(pendingIntent)

            setCallback(object : MediaSession.Callback() {
                override fun onPlay() {
                    val openIntent = Intent(this@AutoBrowserMediaBrowserService, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(openIntent)
                }
            })

            isActive = true
        }

        sessionToken = mediaSession?.sessionToken
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        // Return root for Android Auto head unit and companion app
        return BrowserRoot("autobrowser_root", null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaItem>>
    ) {
        val mediaItems = mutableListOf<MediaItem>()

        val youtubeItem = MediaDescription.Builder()
            .setMediaId("autobrowser_youtube")
            .setTitle("AutoBrowser YouTube Stream")
            .setSubtitle("1080p Full HD In-Car Web Video")
            .build()
        mediaItems.add(MediaItem(youtubeItem, MediaItem.FLAG_PLAYABLE))

        val webItem = MediaDescription.Builder()
            .setMediaId("autobrowser_web")
            .setTitle("AutoBrowser Web Dashboard")
            .setSubtitle("Open Full Web on Car Display")
            .build()
        mediaItems.add(MediaItem(webItem, MediaItem.FLAG_PLAYABLE))

        result.sendResult(mediaItems)
    }

    override fun onDestroy() {
        mediaSession?.release()
        super.onDestroy()
    }
}
