package com.terista.space

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.terista.space.bcore.VirtualEngine
import com.terista.space.device.DeviceInfoManager
import com.terista.space.fs.VirtualFileSystem
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TeristaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var virtualEngine: VirtualEngine

    @Inject
    lateinit var deviceInfoManager: DeviceInfoManager

    @Inject
    lateinit var virtualFileSystem: VirtualFileSystem

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize core virtualization components
        initializeVirtualization()
    }

    private fun initializeVirtualization() {
        // Initialize virtual file system
        virtualFileSystem.initialize()

        // Initialize device info manager
        deviceInfoManager.initialize()

        // Initialize virtual engine (safe, non-hooking version)
        virtualEngine.initialize(this)
    }

    override val workConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    companion object {
        lateinit var instance: TeristaApplication
            private set

        fun getContext(): Context = instance.applicationContext
    }
}
