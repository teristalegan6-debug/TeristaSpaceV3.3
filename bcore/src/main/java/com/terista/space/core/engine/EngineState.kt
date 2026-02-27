package com.terista.space.core.engine

import android.os.Build

/**
 * Represents the current state of the VirtualEngine.
 */
data class EngineState(
    var isInitialized: Boolean = false,
    var supportsWorkProfile: Boolean = false,
    var supportsMultiUser: Boolean = false,
    var isDeviceOwner: Boolean = false,
    var apiLevel: Int = Build.VERSION.SDK_INT,
    var error: Throwable? = null
) {
    fun markInitialized() {
        isInitialized = true
        error = null
    }

    fun markError(e: Throwable) {
        isInitialized = false
        error = e
    }

    override fun toString(): String {
        return "EngineState(initialized=$isInitialized, workProfile=$supportsWorkProfile, " +
               "multiUser=$supportsMultiUser, api=$apiLevel, error=${error?.message})"
    }
}
