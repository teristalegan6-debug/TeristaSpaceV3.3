package com.terista.space.core.utils

import android.util.Log

/**
 * Bridge to native code for low-level operations.
 * Uses legitimate NDK APIs for process management and system info.
 */
class NativeBridge {

    companion object {
        private const val TAG = "NativeBridge"

        init {
            try {
                System.loadLibrary("terista_native")
            } catch (e: UnsatisfiedLinkError) {
                Log.w(TAG, "Native library not loaded: ${e.message}")
            }
        }
    }

    fun initialize(): Boolean {
        return try {
            nativeInit()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Native initialization failed", e)
            false
        }
    }

    fun getSystemInfo(): String {
        return try {
            nativeGetSystemInfo()
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun getProcessStatus(pid: Int): Int {
        return try {
            nativeGetProcessStatus(pid)
        } catch (e: Exception) {
            -1
        }
    }

    // Native methods
    private external fun nativeInit(): Boolean
    private external fun nativeGetSystemInfo(): String
    private external fun nativeGetProcessStatus(pid: Int): Int
    external fun nativeGetMemoryUsage(): Long
    external fun nativeGetCpuUsage(): Double
}
