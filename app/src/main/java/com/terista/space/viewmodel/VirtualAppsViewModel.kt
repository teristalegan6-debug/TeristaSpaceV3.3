package com.terista.space.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.terista.space.data.model.VirtualApp
import com.terista.space.bcore.VirtualAppManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VirtualAppsViewModel @Inject constructor(
    application: Application,
    private val virtualAppManager: VirtualAppManager
) : AndroidViewModel(application) {

    private val _virtualApps = MutableStateFlow<List<VirtualApp>>(emptyList())
    val virtualApps: StateFlow<List<VirtualApp>> = _virtualApps.asStateFlow()

    private val _showInstallDialog = MutableStateFlow(false)
    val showInstallDialog: StateFlow<Boolean> = _showInstallDialog.asStateFlow()

    init {
        loadVirtualApps()
    }

    private fun loadVirtualApps() {
        viewModelScope.launch {
            _virtualApps.value = virtualAppManager.getInstalledVirtualApps()
        }
    }

    fun showInstallDialog() {
        _showInstallDialog.value = true
    }

    fun hideInstallDialog() {
        _showInstallDialog.value = false
    }

    fun installApp(apkPath: String) {
        viewModelScope.launch {
            virtualAppManager.installApp(apkPath)
            loadVirtualApps()
        }
    }

    fun launchApp(app: VirtualApp) {
        viewModelScope.launch {
            val intent = virtualAppManager.getLaunchIntent(app.packageName, app.userId)
            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                getApplication<Application>().startActivity(it)
            }
        }
    }

    fun uninstallApp(app: VirtualApp) {
        viewModelScope.launch {
            virtualAppManager.uninstallApp(app.packageName, app.userId)
            loadVirtualApps()
        }
    }
}
