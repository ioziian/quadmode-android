package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SoundShiftPreferences
import com.example.manager.AudioProfileManager
import com.example.model.ProfileSettings
import com.example.model.VolumeMode
import com.example.service.SoundShiftNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VolumeUiState(
    val currentMode: VolumeMode = VolumeMode.DAY,
    val profileSettingsMap: Map<VolumeMode, ProfileSettings> = emptyMap(),
    val themeMode: String = "DARK", // "DARK" or "LIGHT"
    val isNotificationEnabled: Boolean = true,
    val hasNotificationPolicyAccess: Boolean = true,
    val actualRingPercent: Int = 60,
    val actualNotificationPercent: Int = 50,
    val actualMediaPercent: Int = 65,
    val playingTestStream: Int? = null
) {
    val currentProfile: ProfileSettings
        get() = profileSettingsMap[currentMode] ?: ProfileSettings.defaultForMode(currentMode)
}

class VolumeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = SoundShiftPreferences(application)
    private val audioProfileManager = AudioProfileManager(application)

    private val _uiState = MutableStateFlow(
        VolumeUiState(
            hasNotificationPolicyAccess = audioProfileManager.hasNotificationPolicyAccess()
        )
    )
    val uiState: StateFlow<VolumeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                prefs.currentModeFlow,
                prefs.themeModeFlow,
                prefs.notificationShadeEnabledFlow
            ) { mode, theme, notifEnabled ->
                Triple(mode, theme, notifEnabled)
            }.collect { (mode, theme, notifEnabled) ->
                val outdoor = prefs.getProfileSync(VolumeMode.OUTDOOR)
                val day = prefs.getProfileSync(VolumeMode.DAY)
                val silent = prefs.getProfileSync(VolumeMode.SILENT)
                val night = prefs.getProfileSync(VolumeMode.NIGHT)
                val map = mapOf(
                    VolumeMode.OUTDOOR to outdoor,
                    VolumeMode.DAY to day,
                    VolumeMode.SILENT to silent,
                    VolumeMode.NIGHT to night
                )
                _uiState.update { current ->
                    current.copy(
                        currentMode = mode,
                        themeMode = theme,
                        isNotificationEnabled = notifEnabled,
                        profileSettingsMap = map,
                        hasNotificationPolicyAccess = audioProfileManager.hasNotificationPolicyAccess()
                    )
                }
                refreshActualVolumes()
                val currentProfile = map[mode] ?: ProfileSettings.defaultForMode(mode)
                updateNotificationIfEnabled(mode, currentProfile, notifEnabled)
            }
        }
    }

    fun selectMode(mode: VolumeMode) {
        viewModelScope.launch {
            val profile = _uiState.value.profileSettingsMap[mode] ?: prefs.getProfileSync(mode)
            audioProfileManager.applyProfile(mode, profile)
            prefs.saveCurrentMode(mode)
            refreshActualVolumes()
            updateNotificationIfEnabled(mode, profile, _uiState.value.isNotificationEnabled)
        }
    }

    fun updateCurrentProfile(settings: ProfileSettings) {
        val mode = _uiState.value.currentMode
        viewModelScope.launch {
            prefs.saveProfileSettings(mode, settings)
            audioProfileManager.applyProfile(mode, settings)
            _uiState.update { current ->
                val updatedMap = current.profileSettingsMap.toMutableMap()
                updatedMap[mode] = settings
                current.copy(profileSettingsMap = updatedMap)
            }
            refreshActualVolumes()
            updateNotificationIfEnabled(mode, settings, _uiState.value.isNotificationEnabled)
        }
    }

    fun toggleTheme() {
        val nextTheme = if (_uiState.value.themeMode == "DARK") "LIGHT" else "DARK"
        viewModelScope.launch {
            prefs.saveThemeMode(nextTheme)
        }
    }

    fun toggleNotification(enabled: Boolean) {
        viewModelScope.launch {
            prefs.saveNotificationShadeEnabled(enabled)
            if (enabled) {
                val state = _uiState.value
                updateNotificationIfEnabled(state.currentMode, state.currentProfile, true)
            } else {
                SoundShiftNotificationHelper.cancelNotification(getApplication())
            }
        }
    }

    fun playTestSound(streamType: Int) {
        if (_uiState.value.playingTestStream == streamType) {
            audioProfileManager.stopTestSound()
            _uiState.update { it.copy(playingTestStream = null) }
        } else {
            _uiState.update { it.copy(playingTestStream = streamType) }
            audioProfileManager.playTestSound(streamType) {
                _uiState.update { it.copy(playingTestStream = null) }
            }
        }
    }

    fun refreshActualVolumes() {
        val ring = audioProfileManager.getStreamPercent(android.media.AudioManager.STREAM_RING)
        val notif = audioProfileManager.getStreamPercent(android.media.AudioManager.STREAM_NOTIFICATION)
        val media = audioProfileManager.getStreamPercent(android.media.AudioManager.STREAM_MUSIC)
        val hasPolicy = audioProfileManager.hasNotificationPolicyAccess()

        _uiState.update { current ->
            current.copy(
                actualRingPercent = ring,
                actualNotificationPercent = notif,
                actualMediaPercent = media,
                hasNotificationPolicyAccess = hasPolicy
            )
        }
    }

    private fun updateNotificationIfEnabled(mode: VolumeMode, profile: ProfileSettings, enabled: Boolean) {
        if (!enabled) return
        val details = when (mode) {
            VolumeMode.OUTDOOR -> "Гучність: 100% + Booster (${profile.boosterPercent}%)"
            VolumeMode.DAY -> "Дзвінки: ${profile.ringPercent}%, Медіа: ${profile.mediaPercent}%"
            VolumeMode.SILENT -> "Рівень: 0% (Повна тиша)"
            VolumeMode.NIGHT -> "Нічний спокій / DND увімкнено"
        }
        SoundShiftNotificationHelper.updateNotification(getApplication(), mode, details)
    }
}
