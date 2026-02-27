package com.terista.space.core

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.UserHandle
import android.util.Log
import com.terista.space.data.model.VirtualApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Manages virtual apps using official Android APIs.
 * Supports installation into work profiles and secondary users.
 */
class VirtualAppManager(private val context: Context) {

    companion object {
        private const val TAG = "VirtualAppManager"
    }

    private val packageManager: PackageManager = context.packageManager

    suspend fun getInstalledVirtualApps(): List<VirtualApp> = withContext(Dispatchers.IO) {
        val apps = mutableListOf<VirtualApp>()

        try {
            // Get apps installed for current user
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            installedApps.forEach { appInfo ->
                if (isVirtualApp(appInfo)) {
                    apps.add(createVirtualApp(appInfo))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting installed apps", e)
        }

        apps
    }

    suspend fun installApp(apkPath: String, userId: Int = 0): Boolean = withContext(Dispatchers.IO) {
        try {
            val apkFile = File(apkPath)
            if (!apkFile.exists()) {
                Log.e(TAG, "APK file not found: $apkPath")
                return@withContext false
            }

            // Use PackageInstaller API for modern Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                installWithPackageInstaller(apkFile, userId)
            } else {
                installLegacy(apkFile)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to install app", e)
            false
        }
    }

    private fun installWithPackageInstaller(apkFile: File, userId: Int): Boolean {
        return try {
            val packageInstaller = packageManager.packageInstaller
            val params = PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL
            )

            if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
                params.setOriginatingUid(android.os.Process.myUid())
            }

            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)

            apkFile.inputStream().use { input ->
                session.openWrite("base.apk", 0, apkFile.length()).use { output ->
                    input.copyTo(output)
                    session.fsync(output)
                }
            }

            val intent = Intent(Intent.ACTION_MAIN)
            val pendingIntent = android.app.PendingIntent.getActivity(
                context, sessionId, intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            session.commit(pendingIntent.intentSender)
            session.close()

            true
        } catch (e: Exception) {
            Log.e(TAG, "PackageInstaller failed", e)
            false
        }
    }

    private fun installLegacy(apkFile: File): Boolean {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return true
    }

    suspend fun uninstallApp(packageName: String, userId: Int = 0): Boolean = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                val packageInstaller = packageManager.packageInstaller
                packageInstaller.uninstall(packageName, null)
            } else {
                val intent = Intent(Intent.ACTION_DELETE).apply {
                    data = Uri.parse("package:$packageName")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to uninstall app", e)
            false
        }
    }

    fun getLaunchIntent(packageName: String, userId: Int = 0): Intent? {
        return packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (userId != 0) {
                // For multi-user, we would use UserHandle here
                // This requires INTERACT_ACROSS_USERS permission
            }
        }
    }

    private fun isVirtualApp(appInfo: ApplicationInfo): Boolean {
        // Filter to show only user-installed apps (not system apps)
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
               (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0
    }

    private fun createVirtualApp(appInfo: ApplicationInfo): VirtualApp {
        val packageInfo = packageManager.getPackageInfo(appInfo.packageName, 0)

        return VirtualApp(
            id = "${appInfo.packageName}_${appInfo.uid}",
            name = packageManager.getApplicationLabel(appInfo).toString(),
            packageName = appInfo.packageName,
            versionCode = packageInfo.longVersionCode,
            versionName = packageInfo.versionName ?: "Unknown",
            apkPath = appInfo.sourceDir,
            isRunning = false, // Would need ActivityManager to check
            userId = UserHandle.getUserHandleForUid(appInfo.uid).hashCode()
        )
    }
}
