package com.terista.space.device

import android.content.Context
import android.companion.virtual.VirtualDeviceManager
import android.companion.virtual.VirtualDevice
import android.companion.virtual.VirtualDeviceParams
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * DeviceInfoManager - Manages virtual devices using Android 14+ VirtualDeviceManager API.
 * 
 * This uses the official Android VirtualDeviceManager to create companion virtual devices
 * for testing and development purposes.
 */
class DeviceInfoManager(private val context: Context) {

    companion object {
        private const val TAG = "DeviceInfoManager"
    }

    private var virtualDeviceManager: VirtualDeviceManager? = null
    private val virtualDevices = mutableMapOf<Int, VirtualDevice>()

    private val _deviceState = MutableStateFlow(DeviceState())
    val deviceState: StateFlow<DeviceState> = _deviceState.asStateFlow()

    fun initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            virtualDeviceManager = context.getSystemService(Context.VIRTUAL_DEVICE_SERVICE) as? VirtualDeviceManager
            Log.i(TAG, "VirtualDeviceManager initialized: ${virtualDeviceManager != null}")
        } else {
            Log.w(TAG, "VirtualDeviceManager requires Android 14+")
        }

        updateDeviceState()
    }

    private fun updateDeviceState() {
        _deviceState.value = DeviceState(
            isSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
            virtualDeviceCount = virtualDevices.size,
            hasVirtualDeviceManager = virtualDeviceManager != null
        )
    }

    /**
     * Creates a new virtual device using the official Android 14 VirtualDeviceManager API.
     */
    suspend fun createVirtualDevice(deviceName: String): VirtualDevice? = withContext(Dispatchers.Default) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Log.w(TAG, "VirtualDeviceManager requires Android 14+")
            return@withContext null
        }

        val vdm = virtualDeviceManager ?: run {
            Log.e(TAG, "VirtualDeviceManager not available")
            return@withContext null
        }

        try {
            // Build virtual device parameters using the official API
            val paramsBuilder = VirtualDeviceParams.Builder()
                .setDeviceName(deviceName)
                .setLockState(VirtualDeviceParams.LOCK_STATE_UNLOCKED)
                .setUsersWithMatchingAccounts(emptySet())

            // Create the virtual device
            val virtualDevice = vdm.createVirtualDevice(
                context.mainExecutor,
                paramsBuilder.build()
            )

            virtualDevice?.let {
                virtualDevices[it.deviceId] = it
                updateDeviceState()
                Log.i(TAG, "Created virtual device: ${it.deviceId} - $deviceName")
            }

            virtualDevice
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied creating virtual device", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create virtual device", e)
            null
        }
    }

    /**
     * Gets all active virtual devices.
     */
    fun getVirtualDevices(): List<VirtualDevice> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            virtualDeviceManager?.virtualDevices ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Closes a virtual device.
     */
    fun closeVirtualDevice(deviceId: Int) {
        virtualDevices[deviceId]?.let { device ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                device.close()
                virtualDevices.remove(deviceId)
                updateDeviceState()
                Log.i(TAG, "Closed virtual device: $deviceId")
            }
        }
    }

    /**
     * Closes all virtual devices.
     */
    fun closeAllVirtualDevices() {
        virtualDevices.values.forEach { device ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                device.close()
            }
        }
        virtualDevices.clear()
        updateDeviceState()
        Log.i(TAG, "Closed all virtual devices")
    }

    /**
     * Gets device information for a virtual device.
     */
    fun getDeviceInfo(deviceId: Int): VirtualDeviceInfo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            virtualDevices[deviceId]?.let { device ->
                VirtualDeviceInfo(
                    deviceId = device.deviceId,
                    deviceName = device.deviceName,
                    isActive = true
                )
            }
        } else {
            null
        }
    }

    data class DeviceState(
        val isSupported: Boolean = false,
        val virtualDeviceCount: Int = 0,
        val hasVirtualDeviceManager: Boolean = false
    )

    data class VirtualDeviceInfo(
        val deviceId: Int,
        val deviceName: String,
        val isActive: Boolean
    )
}
