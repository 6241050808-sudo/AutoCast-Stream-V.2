package com.example.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Service for Android Auto projection category matching CarStream's architecture.
 * Registers com.google.android.gms.car.service.CarService with CATEGORY_PROJECTION.
 */
class AutoBrowserCarService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
