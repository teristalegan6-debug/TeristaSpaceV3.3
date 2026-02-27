package com.terista.space.admin

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserHandle
import android.widget.Toast

/**
 * Device Admin Receiver for managing Work Profiles and device policies.
 * This uses official Android Enterprise APIs for legitimate containerization.
 */
class TeristaDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        showToast(context, "Device Admin Enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        showToast(context, "Device Admin Disabled")
    }

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)

        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(context, TeristaDeviceAdminReceiver::class.java)

        // Enable the profile
        devicePolicyManager.setProfileName(adminComponent, "TeristaSpace Work Profile")
        devicePolicyManager.setProfileEnabled(adminComponent)

        // Configure work profile policies
        configureWorkProfilePolicies(devicePolicyManager, adminComponent)

        showToast(context, "Work Profile Created Successfully")
    }

    override fun onPasswordChanged(context: Context, intent: Intent, userHandle: UserHandle) {
        super.onPasswordChanged(context, intent, userHandle)
    }

    override fun onPasswordFailed(context: Context, intent: Intent, userHandle: UserHandle) {
        super.onPasswordFailed(context, intent, userHandle)
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent, userHandle: UserHandle) {
        super.onPasswordSucceeded(context, intent, userHandle)
    }

    private fun configureWorkProfilePolicies(dpm: DevicePolicyManager, admin: ComponentName) {
        // Set password policies
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dpm.setPasswordMinimumLength(admin, 6)
            dpm.setPasswordQuality(admin, DevicePolicyManager.PASSWORD_QUALITY_SOMETHING)
        }

        // Disable camera in work profile if needed
        // dpm.setCameraDisabled(admin, false)

        // Set restrictions
        dpm.addUserRestriction(admin, DevicePolicyManager.DISALLOW_INSTALL_UNKNOWN_SOURCES)
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, TeristaDeviceAdminReceiver::class.java)
        }
    }
}
