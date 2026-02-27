package com.terista.space.di

import android.content.Context
import com.terista.space.bcore.VirtualAppManager
import com.terista.space.bcore.VirtualEngine
import com.terista.space.device.DeviceInfoManager
import com.terista.space.fs.VirtualFileSystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVirtualEngine(@ApplicationContext context: Context): VirtualEngine {
        return VirtualEngine(context)
    }

    @Provides
    @Singleton
    fun provideVirtualAppManager(@ApplicationContext context: Context): VirtualAppManager {
        return VirtualAppManager(context)
    }

    @Provides
    @Singleton
    fun provideDeviceInfoManager(@ApplicationContext context: Context): DeviceInfoManager {
        return DeviceInfoManager(context)
    }

    @Provides
    @Singleton
    fun provideVirtualFileSystem(@ApplicationContext context: Context): VirtualFileSystem {
        return VirtualFileSystem(context)
    }
}
