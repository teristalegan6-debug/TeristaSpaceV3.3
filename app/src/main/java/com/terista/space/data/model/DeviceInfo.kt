package com.terista.space.data.model

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val device: String,
    val brand: String,
    val androidVersion: String,
    val sdkLevel: Int,
    val securityPatch: String,
    val isHardwareAccelerated: Boolean,
    val virtualDeviceCount: Int,
    val hasWorkProfile: Boolean,
    val supportsMultiUser: Boolean
)
