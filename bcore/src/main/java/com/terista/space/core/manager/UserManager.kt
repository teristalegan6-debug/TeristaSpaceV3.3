package com.terista.space.core.manager

import android.content.Context
import android.content.pm.UserInfo
import android.os.Build
import android.os.UserHandle
import android.os.UserManagerHidden
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manages Android user profiles using official UserManager APIs.
 * Supports creating isolated user environments for app sandboxing.
 */
class UserManager(private val context: Context) {

    companion object {
        private const val TAG = "UserManager"
    }

    private val userManager: android.os.UserManager = context.getSystemService(Context.USER_SERVICE) as android.os.UserManager

    fun supportsMultipleUsers(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            userManager.supportsMultipleUsers()
        } else {
            false
        }
    }

    suspend fun createUser(userName: String): Int? = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                // Use UserManagerHidden or reflection for system-level user creation
                // This requires MANAGE_USERS permission
                val userInfo = userManager.createUser(userName, UserInfo.FLAG_RESTRICTED)
                userInfo?.id
            } else {
                Log.w(TAG, "User creation requires Android 5.0+")
                null
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing MANAGE_USERS permission", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create user", e)
            null
        }
    }

    suspend fun removeUser(userId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                userManager.removeUser(userId)
            } else {
                false
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing MANAGE_USERS permission", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove user", e)
            false
        }
    }

    fun getUsers(): List<UserHandle> {
        return if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
            userManager.userProfiles
        } else {
            emptyList()
        }
    }

    fun isRestrictedUser(userId: Int): Boolean {
        return try {
            val userInfo = getUserInfo(userId)
            userInfo?.isRestricted ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun getUserInfo(userId: Int): UserInfo? {
        return try {
            // This requires system permissions
            val method = userManager.javaClass.getMethod("getUserInfo", Int::class.java)
            method.invoke(userManager, userId) as? UserInfo
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user info", e)
            null
        }
    }
}
