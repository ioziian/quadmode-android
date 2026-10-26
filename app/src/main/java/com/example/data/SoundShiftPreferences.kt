package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.ProfileSettings
import com.example.model.VolumeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.soundShiftDataStore: DataStore<Preferences> by preferencesDataStore(name = "sound_shift_prefs_v2")

class SoundShiftPreferences(private val context: Context) {

    private object Keys {
        val CURRENT_MODE = stringPreferencesKey("current_mode")
        val THEME_MODE = stringPreferencesKey("theme_mode") // SYSTEM, DARK, LIGHT
        val NOTIFICATION_SHADE_ENABLED = booleanPreferencesKey("notification_shade_enabled")

        fun ringKey(mode: VolumeMode) = intPreferencesKey("${mode.name}_ring")
        fun notifKey(mode: VolumeMode) = intPreferencesKey("${mode.name}_notif")
        fun mediaKey(mode: VolumeMode) = intPreferencesKey("${mode.name}_media")
        fun boosterKey(mode: VolumeMode) = intPreferencesKey("${mode.name}_booster")
        fun muteKey(mode: VolumeMode) = booleanPreferencesKey("${mode.name}_mute")
        fun dndKey(mode: VolumeMode) = booleanPreferencesKey("${mode.name}_dnd")
        fun vibrateKey(mode: VolumeMode) = booleanPreferencesKey("${mode.name}_vibrate")
        fun darkModeKey(mode: VolumeMode) = booleanPreferencesKey("${mode.name}_dark_mode")
        fun autoBrightnessKey(mode: VolumeMode) = booleanPreferencesKey("${mode.name}_auto_brightness")
        fun batterySaverKey(mode: VolumeMode) = booleanPreferencesKey("${mode.name}_battery_saver")
        fun readingModeKey(mode: VolumeMode) = booleanPreferencesKey("${mode.name}_reading_mode")
        fun airplaneModeKey(mode: VolumeMode) = booleanPreferencesKey("${mode.name}_airplane_mode")
        fun scheduleEnabledKey(mode: VolumeMode) = booleanPreferencesKey("${mode.name}_schedule_enabled")
        fun scheduleStartKey(mode: VolumeMode) = stringPreferencesKey("${mode.name}_schedule_start")
        fun scheduleEndKey(mode: VolumeMode) = stringPreferencesKey("${mode.name}_schedule_end")
        fun scheduleDaysKey(mode: VolumeMode) = stringPreferencesKey("${mode.name}_schedule_days")
    }

    val currentModeFlow: Flow<VolumeMode> = context.soundShiftDataStore.data.map { prefs ->
        val modeStr = prefs[Keys.CURRENT_MODE] ?: VolumeMode.DAY.name
        try {
            VolumeMode.valueOf(modeStr)
        } catch (_: Exception) {
            VolumeMode.DAY
        }
    }

    val themeModeFlow: Flow<String> = context.soundShiftDataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE] ?: "SYSTEM"
    }

    val notificationShadeEnabledFlow: Flow<Boolean> = context.soundShiftDataStore.data.map { prefs ->
        prefs[Keys.NOTIFICATION_SHADE_ENABLED] ?: true
    }

    fun profileSettingsFlow(mode: VolumeMode): Flow<ProfileSettings> = context.soundShiftDataStore.data.map { prefs ->
        val default = ProfileSettings.defaultForMode(mode)
        val daysString = prefs[Keys.scheduleDaysKey(mode)] ?: "1,2,3,4,5,6,7"
        val daysSet = daysString.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()

        ProfileSettings(
            ringPercent = prefs[Keys.ringKey(mode)] ?: default.ringPercent,
            notificationPercent = prefs[Keys.notifKey(mode)] ?: default.notificationPercent,
            mediaPercent = prefs[Keys.mediaKey(mode)] ?: default.mediaPercent,
            boosterPercent = prefs[Keys.boosterKey(mode)] ?: default.boosterPercent,
            isMute = prefs[Keys.muteKey(mode)] ?: default.isMute,
            isDnd = prefs[Keys.dndKey(mode)] ?: default.isDnd,
            isVibrate = prefs[Keys.vibrateKey(mode)] ?: default.isVibrate,
            isDarkMode = prefs[Keys.darkModeKey(mode)] ?: default.isDarkMode,
            isAutoBrightness = prefs[Keys.autoBrightnessKey(mode)] ?: default.isAutoBrightness,
            isBatterySaver = prefs[Keys.batterySaverKey(mode)] ?: default.isBatterySaver,
            isReadingMode = prefs[Keys.readingModeKey(mode)] ?: default.isReadingMode,
            isAirplaneMode = prefs[Keys.airplaneModeKey(mode)] ?: default.isAirplaneMode,
            isScheduleEnabled = prefs[Keys.scheduleEnabledKey(mode)] ?: default.isScheduleEnabled,
            scheduleStartTime = prefs[Keys.scheduleStartKey(mode)] ?: default.scheduleStartTime,
            scheduleEndTime = prefs[Keys.scheduleEndKey(mode)] ?: default.scheduleEndTime,
            scheduleDays = if (daysSet.isNotEmpty()) daysSet else default.scheduleDays
        )
    }

    suspend fun getProfileSync(mode: VolumeMode): ProfileSettings {
        return profileSettingsFlow(mode).first()
    }

    suspend fun getCurrentModeSync(): VolumeMode {
        return currentModeFlow.first()
    }

    suspend fun isNotificationShadeEnabledSync(): Boolean {
        return notificationShadeEnabledFlow.first()
    }

    suspend fun saveCurrentMode(mode: VolumeMode) {
        context.soundShiftDataStore.edit { prefs ->
            prefs[Keys.CURRENT_MODE] = mode.name
        }
    }

    suspend fun saveThemeMode(themeMode: String) {
        context.soundShiftDataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = themeMode
        }
    }

    suspend fun saveNotificationShadeEnabled(enabled: Boolean) {
        context.soundShiftDataStore.edit { prefs ->
            prefs[Keys.NOTIFICATION_SHADE_ENABLED] = enabled
        }
    }

    suspend fun saveProfileSettings(mode: VolumeMode, settings: ProfileSettings) {
        context.soundShiftDataStore.edit { prefs ->
            prefs[Keys.ringKey(mode)] = settings.ringPercent.coerceIn(0, 100)
            prefs[Keys.notifKey(mode)] = settings.notificationPercent.coerceIn(0, 100)
            prefs[Keys.mediaKey(mode)] = settings.mediaPercent.coerceIn(0, 100)
            prefs[Keys.boosterKey(mode)] = settings.boosterPercent.coerceIn(100, 200)
            prefs[Keys.muteKey(mode)] = settings.isMute
            prefs[Keys.dndKey(mode)] = settings.isDnd
            prefs[Keys.vibrateKey(mode)] = settings.isVibrate
            prefs[Keys.darkModeKey(mode)] = settings.isDarkMode
            prefs[Keys.autoBrightnessKey(mode)] = settings.isAutoBrightness
            prefs[Keys.batterySaverKey(mode)] = settings.isBatterySaver
            prefs[Keys.readingModeKey(mode)] = settings.isReadingMode
            prefs[Keys.airplaneModeKey(mode)] = settings.isAirplaneMode
            prefs[Keys.scheduleEnabledKey(mode)] = settings.isScheduleEnabled
            prefs[Keys.scheduleStartKey(mode)] = settings.scheduleStartTime
            prefs[Keys.scheduleEndKey(mode)] = settings.scheduleEndTime
            prefs[Keys.scheduleDaysKey(mode)] = settings.scheduleDays.joinToString(",")
        }
    }
}
