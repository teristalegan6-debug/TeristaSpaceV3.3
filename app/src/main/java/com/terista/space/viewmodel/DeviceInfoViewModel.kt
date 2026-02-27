package com.terista.space.viewmodel

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terista.space.data.model.DeviceInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceInfoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _deviceInfo = MutableStateFlow(getCurrentDeviceInfo())
    val deviceInfo: StateFlow<DeviceInfo> = _deviceInfo.asStateFlow()

    private fun getCurrentDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            device = Build.DEVICE,
            brand = Build.BRAND,
            androidVersion = Build.VERSION.RELEASE,
            sdkLevel = Build.VERSION.SDK_INT,
            securityPatch = Build.VERSION.SECURITY_PATCH ?: "Unknown",
            isHardwareAccelerated = Build.SUPPORTED_ABIS.any { it.contains("64") },
            virtualDeviceCount = 0,
            hasWorkProfile = false,
            supportsMultiUser = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        )
    }

    init {
        viewModelScope.launch {
            refreshDeviceInfo()
        }
    }

    private fun refreshDeviceInfo() {
        viewModelScope.launch {
            _deviceInfo.value = getCurrentDeviceInfo()
        }
    }
}
