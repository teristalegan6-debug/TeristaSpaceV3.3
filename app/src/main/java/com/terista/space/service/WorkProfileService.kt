package com.terista.space.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

/**
 * Service for managing Work Profile provisioning and lifecycle.
 */
class WorkProfileService : Service() {

    companion object {
        private const val TAG = "WorkProfileService"
    }

    private val binder = WorkProfileBinder()

    inner class WorkProfileBinder : Binder() {
        fun getService(): WorkProfileService = this@WorkProfileService
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "WorkProfileService created")
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "WorkProfileService destroyed")
    }
}
