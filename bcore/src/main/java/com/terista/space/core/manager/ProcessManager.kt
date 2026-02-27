package com.terista.space.core.manager

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manages virtual processes and app lifecycle.
 * Uses official ActivityManager APIs for process monitoring.
 */
class ProcessManager(private val context: Context) {

    companion object {
        private const val TAG = "ProcessManager"
    }

    private val activityManager: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    fun initialize() {
        Log.i(TAG, "Initializing ProcessManager")
    }

    suspend fun getRunningApps(): List<RunningAppInfo> = withContext(Dispatchers.IO) {
        val apps = mutableListOf<RunningAppInfo>()

        try {
            if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                // Use getRunningAppProcesses for modern Android
                activityManager.runningAppProcesses?.forEach { process ->
                    apps.add(
                        RunningAppInfo(
                            processName = process.processName,
                            pid = process.pid,
                            uid = process.uid,
                            packageNames = process.pkgList?.toList() ?: emptyList(),
                            importance = process.importance
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied getting running apps", e)
        }

        apps
    }

    fun killProcess(pid: Int): Boolean {
        return try {
            Process.killProcess(pid)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to kill process $pid", e)
            false
        }
    }

    fun getMemoryInfo(): MemoryInfo {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        return MemoryInfo(
            totalMemory = memoryInfo.totalMem,
            availableMemory = memoryInfo.availMem,
            lowMemory = memoryInfo.lowMemory,
            threshold = memoryInfo.threshold
        )
    }

    data class RunningAppInfo(
        val processName: String,
        val pid: Int,
        val uid: Int,
        val packageNames: List<String>,
        val importance: Int
    )

    data class MemoryInfo(
        val totalMemory: Long,
        val availableMemory: Long,
        val lowMemory: Boolean,
        val threshold: Long
    )
}
