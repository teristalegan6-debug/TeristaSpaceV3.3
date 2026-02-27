package com.terista.space.viewmodel

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terista.space.admin.TeristaDeviceAdminReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val adminComponent = ComponentName(context, TeristaDeviceAdminReceiver::class.java)

    private val _hasWorkProfile = MutableStateFlow(false)
    val hasWorkProfile: StateFlow<Boolean> = _hasWorkProfile.asStateFlow()

    init {
        checkWorkProfileStatus()
    }

    private fun checkWorkProfileStatus() {
        viewModelScope.launch {
            _hasWorkProfile.value = devicePolicyManager.isAdminActive(adminComponent)
        }
    }

    fun hasActiveWorkProfile(): Boolean {
        return devicePolicyManager.isAdminActive(adminComponent)
    }

    fun provisionWorkProfile() {
        val intent = Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE).apply {
            putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME, adminComponent)
            putExtra(DevicePolicyManager.EXTRA_PROVISIONING_SKIP_ENCRYPTION, false)
            putExtra(DevicePolicyManager.EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED, false)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun removeWorkProfile() {
        if (devicePolicyManager.isAdminActive(adminComponent)) {
            devicePolicyManager.wipeData(0)
            devicePolicyManager.removeActiveAdmin(adminComponent)
            _hasWorkProfile.value = false
        }
    }

    fun openWorkProfileSettings() {
        val intent = Intent().apply {
            action = DevicePolicyManager.ACTION_SET_NEW_PASSWORD
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
