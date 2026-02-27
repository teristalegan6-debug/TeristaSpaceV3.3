package com.terista.space.data.model

data class VirtualApp(
    val id: String,
    val name: String,
    val packageName: String,
    val versionCode: Long,
    val versionName: String,
    val iconPath: String? = null,
    val apkPath: String,
    val isRunning: Boolean = false,
    val userId: Int = 0,
    val installTime: Long = System.currentTimeMillis()
)
