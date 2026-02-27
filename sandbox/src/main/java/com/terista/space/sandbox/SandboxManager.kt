package com.terista.space.sandbox

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Manages the sandboxed environment using Android's isolatedProcess feature.
 * 
 * This manager handles binding to the SandboxService which runs in a separate,
 * isolated process with no permissions and unique UID.
 */
class SandboxManager(private val context: Context) {

    companion object {
        private const val TAG = "SandboxManager"
        private const val BIND_TIMEOUT_MS = 5000L
    }

    private var sandboxService: SandboxService.SandboxBinder? = null
    private var serviceConnection: ServiceConnection? = null
    private val bindDeferred = CompletableDeferred<Boolean>()

    /**
     * Binds to the sandbox service.
     */
    suspend fun bindSandbox(): Boolean = withContext(Dispatchers.Default) {
        val intent = Intent(context, SandboxService::class.java)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                sandboxService = service as SandboxService.SandboxBinder
                bindDeferred.complete(true)
                Log.i(TAG, "Sandbox service connected")
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                sandboxService = null
                Log.i(TAG, "Sandbox service disconnected")
            }
        }

        val bound = context.bindService(
            intent,
            serviceConnection!!,
            Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
        )

        if (bound) {
            withTimeoutOrNull(BIND_TIMEOUT_MS) {
                bindDeferred.await()
            } ?: false
        } else {
            false
        }
    }

    /**
     * Unbinds from the sandbox service.
     */
    fun unbindSandbox() {
        serviceConnection?.let {
            context.unbindService(it)
            sandboxService = null
            serviceConnection = null
            Log.i(TAG, "Sandbox service unbound")
        }
    }

    /**
     * Executes code in the sandbox.
     */
    suspend fun executeInSandbox(code: String): SandboxService.SandboxResult {
        return sandboxService?.executeInSandbox(code) 
            ?: SandboxService.SandboxResult.Error("Sandbox not connected")
    }

    /**
     * Gets information about the sandbox environment.
     */
    fun getSandboxInfo(): SandboxService.SandboxInfo? {
        return sandboxService?.getSandboxInfo()
    }

    /**
     * Checks if sandbox is currently connected.
     */
    fun isSandboxConnected(): Boolean {
        return sandboxService != null
    }
}
