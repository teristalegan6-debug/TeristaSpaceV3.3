package com.terista.space.data.model

data class Settings(
    val hardwareAcceleration: Boolean = true,
    val autoStartApps: Boolean = false,
    val isolateFileSystem: Boolean = true,
    val debugMode: Boolean = false,
    val defaultUserId: Int = 0
)
