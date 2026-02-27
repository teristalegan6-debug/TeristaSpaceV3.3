package com.terista.space.fs

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * VirtualFileSystem - Manages isolated file system spaces for virtual environments.
 * 
 * This creates sandboxed directories for apps running in virtual contexts,
 * ensuring file system isolation between different virtual users.
 */
class VirtualFileSystem(private val context: Context) {

    companion object {
        private const val TAG = "VirtualFileSystem"
        private const val VIRTUAL_ROOT = "virtual_spaces"
        private const val APPS_DIR = "apps"
        private const val DATA_DIR = "data"
        private const val CACHE_DIR = "cache"
    }

    private val _vfsState = MutableStateFlow(VfsState())
    val vfsState: StateFlow<VfsState> = _vfsState.asStateFlow()

    private val virtualRootDir: File by lazy {
        File(context.filesDir, VIRTUAL_ROOT).apply {
            if (!exists()) mkdirs()
        }
    }

    fun initialize() {
        Log.i(TAG, "Initializing VirtualFileSystem")
        ensureDirectories()
        updateState()
    }

    private fun ensureDirectories() {
        listOf(APPS_DIR, DATA_DIR, CACHE_DIR).forEach { dirName ->
            File(virtualRootDir, dirName).apply {
                if (!exists()) mkdirs()
            }
        }
    }

    private fun updateState() {
        _vfsState.value = VfsState(
            isInitialized = true,
            rootPath = virtualRootDir.absolutePath,
            availableSpace = virtualRootDir.freeSpace
        )
    }

    /**
     * Creates a virtual file space for a specific user/app combination.
     */
    fun createVirtualSpace(userId: Int, packageName: String): VirtualSpace {
        val spaceDir = File(virtualRootDir, "$DATA_DIR/user_$userId/$packageName").apply {
            if (!exists()) mkdirs()
        }

        val cacheDir = File(spaceDir, "cache").apply {
            if (!exists()) mkdirs()
        }

        val filesDir = File(spaceDir, "files").apply {
            if (!exists()) mkdirs()
        }

        return VirtualSpace(
            userId = userId,
            packageName = packageName,
            rootDir = spaceDir,
            cacheDir = cacheDir,
            filesDir = filesDir
        )
    }

    /**
     * Gets the directory for installing APKs in a virtual space.
     */
    fun getAppInstallDir(userId: Int): File {
        return File(virtualRootDir, "$APPS_DIR/user_$userId").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Copies a file into a virtual space.
     */
    suspend fun copyToVirtualSpace(
        sourceUri: Uri,
        destinationPath: String,
        userId: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            val destFile = File(virtualRootDir, "$DATA_DIR/user_$userId/$destinationPath").apply {
                parentFile?.mkdirs()
            }

            inputStream?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to copy file to virtual space", e)
            false
        }
    }

    /**
     * Gets a URI for a file in the virtual space (for sharing).
     */
    fun getFileUri(path: String, userId: Int): Uri {
        val file = File(virtualRootDir, "$DATA_DIR/user_$userId/$path")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.virtualfiles",
            file
        )
    }

    /**
     * Clears all data for a specific user.
     */
    suspend fun clearUserData(userId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val userDir = File(virtualRootDir, "$DATA_DIR/user_$userId")
            if (userDir.exists()) {
                userDir.deleteRecursively()
            }
            val appDir = File(virtualRootDir, "$APPS_DIR/user_$userId")
            if (appDir.exists()) {
                appDir.deleteRecursively()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear user data", e)
            false
        }
    }

    /**
     * Gets storage statistics for a virtual space.
     */
    fun getSpaceStats(userId: Int, packageName: String): SpaceStats {
        val spaceDir = File(virtualRootDir, "$DATA_DIR/user_$userId/$packageName")
        return SpaceStats(
            totalSize = calculateSize(spaceDir),
            cacheSize = calculateSize(File(spaceDir, "cache")),
            filesSize = calculateSize(File(spaceDir, "files"))
        )
    }

    private fun calculateSize(dir: File): Long {
        if (!dir.exists()) return 0
        return dir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }

    data class VirtualSpace(
        val userId: Int,
        val packageName: String,
        val rootDir: File,
        val cacheDir: File,
        val filesDir: File
    )

    data class VfsState(
        val isInitialized: Boolean = false,
        val rootPath: String = "",
        val availableSpace: Long = 0
    )

    data class SpaceStats(
        val totalSize: Long,
        val cacheSize: Long,
        val filesSize: Long
    )
}
