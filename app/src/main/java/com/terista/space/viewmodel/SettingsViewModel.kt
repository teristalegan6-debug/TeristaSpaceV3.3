package com.terista.space.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terista.space.data.model.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    private val hardwareAccelerationKey = booleanPreferencesKey("hardware_acceleration")
    private val autoStartAppsKey = booleanPreferencesKey("auto_start_apps")
    private val isolateFileSystemKey = booleanPreferencesKey("isolate_file_system")
    private val debugModeKey = booleanPreferencesKey("debug_mode")
    private val defaultUserIdKey = intPreferencesKey("default_user_id")

    init {
        viewModelScope.launch {
            context.dataStore.data.map { prefs ->
                Settings(
                    hardwareAcceleration = prefs[hardwareAccelerationKey] ?: true,
                    autoStartApps = prefs[autoStartAppsKey] ?: false,
                    isolateFileSystem = prefs[isolateFileSystemKey] ?: true,
                    debugMode = prefs[debugModeKey] ?: false,
                    defaultUserId = prefs[defaultUserIdKey] ?: 0
                )
            }.collect { _settings.value = it }
        }
    }

    fun setHardwareAcceleration(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[hardwareAccelerationKey] = enabled
            }
        }
    }

    fun setAutoStartApps(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[autoStartAppsKey] = enabled
            }
        }
    }

    fun setIsolateFileSystem(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[isolateFileSystemKey] = enabled
            }
        }
    }

    fun setDebugMode(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[debugModeKey] = enabled
            }
        }
    }
}
