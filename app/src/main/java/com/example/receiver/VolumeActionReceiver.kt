package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.SoundShiftPreferences
import com.example.manager.AudioProfileManager
import com.example.model.VolumeMode
import com.example.service.SoundShiftNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VolumeActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SET_OUTDOOR = "com.example.soundshift.ACTION_SET_OUTDOOR"
        const val ACTION_SET_DAY = "com.example.soundshift.ACTION_SET_DAY"
        const val ACTION_SET_SILENT = "com.example.soundshift.ACTION_SET_SILENT"
        const val ACTION_SET_NIGHT = "com.example.soundshift.ACTION_SET_NIGHT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val prefs = SoundShiftPreferences(context.applicationContext)
        val audioManager = AudioProfileManager(context.applicationContext)

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newMode = when (action) {
                    ACTION_SET_OUTDOOR -> VolumeMode.OUTDOOR
                    ACTION_SET_DAY -> VolumeMode.DAY
                    ACTION_SET_SILENT -> VolumeMode.SILENT
                    ACTION_SET_NIGHT -> VolumeMode.NIGHT
                    else -> prefs.getCurrentModeSync()
                }

                val profileSettings = prefs.getProfileSync(newMode)
                audioManager.applyProfile(newMode, profileSettings)
                prefs.saveCurrentMode(newMode)

                val isNotifEnabled = prefs.isNotificationShadeEnabledSync()
                if (isNotifEnabled) {
                    val details = when (newMode) {
                        VolumeMode.OUTDOOR -> "Гучність: 100% + Супер-підсилення"
                        VolumeMode.DAY -> "Дзвінки: ${profileSettings.ringPercent}%, Медіа: ${profileSettings.mediaPercent}%"
                        VolumeMode.SILENT -> "Рівень: 0% (Повна тиша)"
                        VolumeMode.NIGHT -> "Нічний спокій / DND увімкнено"
                    }
                    SoundShiftNotificationHelper.updateNotification(context.applicationContext, newMode, details)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
