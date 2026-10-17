package com.example.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.browse.MediaBrowser.MediaItem
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.util.Log
import com.example.MainActivity

/**
 * Robust MediaBrowserService implementation for Android Auto.
 * Fixes "โหลดรายการที่คุณเลือกไม่ได้" by implementing full PlaybackState, MediaMetadata,
 * and MediaSession.Callback (onPlayFromMediaId, onPrepare, onPlay).
 */
class AutoBrowserMediaBrowserService : MediaBrowserService() {

    private var mediaSession: MediaSession? = null
    private var audioTrack: AudioTrack? = null
    @Volatile
    private var isAudioStreaming = false
    private var currentMediaId = "autobrowser_youtube"

    companion object {
        private const val TAG = "AutoBrowserMediaService"
        const val ROOT_ID = "autobrowser_root"
        const val MEDIA_ID_YOUTUBE = "autobrowser_youtube"
        const val MEDIA_ID_WEB = "autobrowser_web"
        const val MEDIA_ID_MAPS = "autobrowser_maps"
        const val MEDIA_ID_SPOTIFY = "autobrowser_spotify"
        const val MEDIA_ID_RESTART = "autobrowser_restart"
    }

    override fun onCreate() {
        super.onCreate()

        // Ensure Presentation Service is active to monitor car screens
        try {
            AutoBrowserPresentationService.startService(applicationContext)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start presentation service: ${e.message}")
        }

        // Configure MediaSession for Android Auto
        val session = MediaSession(this, "AutoBrowserMediaSession")
        mediaSession = session

        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        session.setSessionActivity(pendingIntent)

        // Set initial playback state with all supported actions
        val initialPlaybackState = buildPlaybackState(PlaybackState.STATE_PAUSED, 0L)
        session.setPlaybackState(initialPlaybackState)

        // Set initial metadata so Android Auto renders without loading errors
        updateMetadata("YouTube 1080p 60fps", "แตะเพื่อเปิดหน้าจอวิดีโอบนจอรถยนต์")

        session.setCallback(object : MediaSession.Callback() {
            override fun onPlay() {
                playMedia(currentMediaId)
            }

            override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                Log.d(TAG, "onPlayFromMediaId: $mediaId")
                playMedia(mediaId ?: MEDIA_ID_YOUTUBE)
            }

            override fun onPlayFromSearch(query: String?, extras: Bundle?) {
                val url = if (query.isNullOrBlank()) {
                    "https://www.youtube.com"
                } else {
                    "https://www.google.com/search?q=" + Uri.encode(query)
                }
                launchBrowserWithUrl(url)
                updateMetadata("ค้นหา: $query", "กำลังเปิดเว็บบนจอรถยนต์")
                setPlaybackActive()
            }

            override fun onPrepare() {
                setPlaybackActive()
            }

            override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
                currentMediaId = mediaId ?: MEDIA_ID_YOUTUBE
                setPlaybackActive()
            }

            override fun onPause() {
                stopSilentAudio()
                session.setPlaybackState(buildPlaybackState(PlaybackState.STATE_PAUSED, 0L))
            }

            override fun onStop() {
                stopSilentAudio()
                session.setPlaybackState(buildPlaybackState(PlaybackState.STATE_STOPPED, 0L))
            }

            override fun onSkipToNext() {
                val nextMedia = when (currentMediaId) {
                    MEDIA_ID_YOUTUBE -> MEDIA_ID_WEB
                    MEDIA_ID_WEB -> MEDIA_ID_MAPS
                    MEDIA_ID_MAPS -> MEDIA_ID_SPOTIFY
                    else -> MEDIA_ID_YOUTUBE
                }
                playMedia(nextMedia)
            }

            override fun onSkipToPrevious() {
                val prevMedia = when (currentMediaId) {
                    MEDIA_ID_SPOTIFY -> MEDIA_ID_MAPS
                    MEDIA_ID_MAPS -> MEDIA_ID_WEB
                    MEDIA_ID_WEB -> MEDIA_ID_YOUTUBE
                    else -> MEDIA_ID_SPOTIFY
                }
                playMedia(prevMedia)
            }

            override fun onCustomAction(action: String, extras: Bundle?) {
                playMedia(currentMediaId)
            }
        })

        session.isActive = true
        sessionToken = session.sessionToken
    }

    private fun playMedia(mediaId: String) {
        currentMediaId = mediaId
        val (url, title, subtitle) = when (mediaId) {
            MEDIA_ID_YOUTUBE -> Triple(
                "https://www.youtube.com",
                "🎬 YouTube 1080p 60fps",
                "กำลังแสดงผลวิดีโอบนหน้าจอ Android Auto"
            )
            MEDIA_ID_WEB -> Triple(
                "https://www.google.com",
                "🌐 เว็บเบราว์เซอร์ (Google Web)",
                "กำลังแสดงผลเว็บเบราว์เซอร์บนจอรถ"
            )
            MEDIA_ID_MAPS -> Triple(
                "https://maps.google.com",
                "🗺️ แผนที่ Google Maps สด",
                "แสดงเส้นทางและการจราจรสดบนจอรถ"
            )
            MEDIA_ID_SPOTIFY -> Triple(
                "https://open.spotify.com",
                "🎵 Spotify Web Player",
                "เครื่องเล่นเพลงคุณภาพสูงบนจอรถ"
            )
            MEDIA_ID_RESTART -> {
                // Restart presentation service
                try {
                    AutoBrowserPresentationService.stopService(applicationContext)
                    AutoBrowserPresentationService.startService(applicationContext)
                } catch (_: Exception) {}
                Triple(
                    "https://www.youtube.com",
                    "⚡ รีสตาร์ทระบบฉายภาพเรียบร้อย",
                    "ระบบพร้อมแสดงผลบนจอรถยนต์แล้ว"
                )
            }
            else -> Triple(
                "https://www.youtube.com",
                "AutoBrowser Stream",
                "กำลังแสดงผลบนหน้าจอ Android Auto"
            )
        }

        updateMetadata(title, subtitle)
        setPlaybackActive()
        startSilentAudio()
        launchBrowserWithUrl(url)
    }

    private fun setPlaybackActive() {
        val playingState = buildPlaybackState(PlaybackState.STATE_PLAYING, 0L)
        mediaSession?.setPlaybackState(playingState)
    }

    private fun buildPlaybackState(state: Int, position: Long): PlaybackState {
        return PlaybackState.Builder()
            .setActions(
                PlaybackState.ACTION_PLAY or
                PlaybackState.ACTION_PAUSE or
                PlaybackState.ACTION_PLAY_PAUSE or
                PlaybackState.ACTION_SKIP_TO_NEXT or
                PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackState.ACTION_PLAY_FROM_SEARCH or
                PlaybackState.ACTION_PREPARE or
                PlaybackState.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackState.ACTION_STOP
            )
            .setState(state, position, 1.0f)
            .build()
    }

    private fun updateMetadata(title: String, artist: String) {
        val metadata = MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, currentMediaId)
            .putString(MediaMetadata.METADATA_KEY_TITLE, title)
            .putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, "AutoBrowser for Android Auto")
            .putLong(MediaMetadata.METADATA_KEY_DURATION, -1L) // Continuous stream
            .build()
        mediaSession?.setMetadata(metadata)
    }

    private fun launchBrowserWithUrl(url: String) {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra("NAVIGATE_URL", url)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch MainActivity: ${e.message}")
        }
    }

    /**
     * Generates a gentle silent PCM audio stream so Android Auto's car audio router
     * keeps audio focus and recognizes the media app as active without timing out.
     */
    private fun startSilentAudio() {
        if (isAudioStreaming) return
        isAudioStreaming = true

        Thread {
            try {
                val sampleRate = 44100
                val minBufSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()
                val silentBuffer = ByteArray(minBufSize)

                while (isAudioStreaming) {
                    track.write(silentBuffer, 0, silentBuffer.size)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Silent audio thread exited: ${e.message}")
            }
        }.start()
    }

    private fun stopSilentAudio() {
        isAudioStreaming = false
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        val rootExtras = Bundle().apply {
            putBoolean("android.media.browse.SEARCH_SUPPORTED", true)
            putBoolean("android.service.media.extra.OFFLINE", true)
            putBoolean("android.service.media.extra.RECENT", true)
        }
        return BrowserRoot(ROOT_ID, rootExtras)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaItem>>
    ) {
        val mediaItems = mutableListOf<MediaItem>()

        // 1. YouTube Option
        val youtubeItem = MediaDescription.Builder()
            .setMediaId(MEDIA_ID_YOUTUBE)
            .setTitle("🎬 เปิด YouTube (1080p 60fps)")
            .setSubtitle("รับชมวิดีโอและเพลง YouTube เต็มจอรถยนต์")
            .build()
        mediaItems.add(MediaItem(youtubeItem, MediaItem.FLAG_PLAYABLE))

        // 2. Web Browser Option
        val webItem = MediaDescription.Builder()
            .setMediaId(MEDIA_ID_WEB)
            .setTitle("🌐 เว็บเบราว์เซอร์ (Google Web)")
            .setSubtitle("ค้นหาและเปิดดูเว็บไซต์ทั่วไปบนจอรถ")
            .build()
        mediaItems.add(MediaItem(webItem, MediaItem.FLAG_PLAYABLE))

        // 3. Google Maps Option
        val mapsItem = MediaDescription.Builder()
            .setMediaId(MEDIA_ID_MAPS)
            .setTitle("🗺️ แผนที่ Google Maps สด")
            .setSubtitle("ตรวจสอบสภาพการจราจรและเส้นทางสด")
            .build()
        mediaItems.add(MediaItem(mapsItem, MediaItem.FLAG_PLAYABLE))

        // 4. Spotify Option
        val spotifyItem = MediaDescription.Builder()
            .setMediaId(MEDIA_ID_SPOTIFY)
            .setTitle("🎵 Spotify Web Music")
            .setSubtitle("เครื่องเล่นเพลงความคมชัดสูง")
            .build()
        mediaItems.add(MediaItem(spotifyItem, MediaItem.FLAG_PLAYABLE))

        // 5. Quick Restart Option
        val restartItem = MediaDescription.Builder()
            .setMediaId(MEDIA_ID_RESTART)
            .setTitle("⚡ รีเฟรชการแสดงผลจอรถยนต์")
            .setSubtitle("เชื่อมต่อใหม่หากหน้าจอรถยังไม่แสดงภาพ")
            .build()
        mediaItems.add(MediaItem(restartItem, MediaItem.FLAG_PLAYABLE))

        result.sendResult(mediaItems)
    }

    override fun onDestroy() {
        stopSilentAudio()
        mediaSession?.release()
        super.onDestroy()
    }
}
