package com.terista.space.device

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

/**
 * Service for managing virtual devices in the background.
 * Handles virtual device lifecycle and communication.
 */
class VirtualDeviceService : Service() {

    companion object {
        private const val TAG = "VirtualDeviceService"
    }

    private val binder = VirtualDeviceBinder()

    inner class VirtualDeviceBinder : Binder() {
        fun getService(): VirtualDeviceService = this@VirtualDeviceService
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "VirtualDeviceService created")
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "VirtualDeviceService destroyed")
    }
}
