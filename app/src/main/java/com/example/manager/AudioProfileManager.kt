package com.example.manager

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.model.ProfileSettings
import com.example.model.VolumeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioProfileManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var activeTestRingtone: Ringtone? = null
    private var testStopJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun hasNotificationPolicyAccess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.isNotificationPolicyAccessGranted
        } else {
            true
        }
    }

    fun applyProfile(mode: VolumeMode, settings: ProfileSettings) {
        try {
            when {
                settings.isMute || mode == VolumeMode.SILENT -> {
                    if (settings.isVibrate) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                    } else if (hasNotificationPolicyAccess()) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                    } else {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                    }
                }
                settings.isDnd || mode == VolumeMode.NIGHT -> {
                    if (hasNotificationPolicyAccess() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                    }
                    audioManager.ringerMode = if (settings.isVibrate) AudioManager.RINGER_MODE_VIBRATE else AudioManager.RINGER_MODE_SILENT
                }
                else -> {
                    if (hasNotificationPolicyAccess() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                    }
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
            }
        } catch (e: Exception) {
            Log.w("AudioProfileManager", "Could not set ringer mode: ${e.message}")
        }

        // Apply stream volumes with 1% accuracy
        val targetRing = if (settings.isMute) 0 else settings.ringPercent
        val targetNotif = if (settings.isMute) 0 else settings.notificationPercent
        val targetMedia = if (settings.isMute) 0 else settings.mediaPercent

        setStreamPercent(AudioManager.STREAM_RING, targetRing)
        setStreamPercent(AudioManager.STREAM_NOTIFICATION, targetNotif)
        setStreamPercent(AudioManager.STREAM_MUSIC, targetMedia)
    }

    fun setStreamPercent(streamType: Int, percent: Int) {
        try {
            val max = audioManager.getStreamMaxVolume(streamType)
            val min = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                audioManager.getStreamMinVolume(streamType)
            } else {
                0
            }
            val target = (min + ((max - min) * (percent.coerceIn(0, 100) / 100f))).toInt().coerceIn(min, max)
            audioManager.setStreamVolume(streamType, target, 0)
        } catch (e: Exception) {
            Log.w("AudioProfileManager", "Error setting stream volume: ${e.message}")
        }
    }

    fun getStreamPercent(streamType: Int): Int {
        return try {
            val max = audioManager.getStreamMaxVolume(streamType)
            val min = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                audioManager.getStreamMinVolume(streamType)
            } else {
                0
            }
            val current = audioManager.getStreamVolume(streamType)
            if (max == min) 0 else (((current - min).toFloat() / (max - min)) * 100).toInt().coerceIn(0, 100)
        } catch (e: Exception) {
            50
        }
    }

    fun playTestSound(streamType: Int, onComplete: () -> Unit = {}) {
        stopTestSound()
        try {
            val ringtoneType = when (streamType) {
                AudioManager.STREAM_RING -> RingtoneManager.TYPE_RINGTONE
                AudioManager.STREAM_NOTIFICATION -> RingtoneManager.TYPE_NOTIFICATION
                else -> RingtoneManager.TYPE_ALARM
            }
            var alertUri: Uri? = RingtoneManager.getActualDefaultRingtoneUri(context, ringtoneType)
            if (alertUri == null) {
                alertUri = RingtoneManager.getDefaultUri(ringtoneType)
            }
            if (alertUri != null) {
                val ringtone = RingtoneManager.getRingtone(context, alertUri)
                ringtone?.streamType = streamType
                ringtone?.play()
                activeTestRingtone = ringtone

                testStopJob = scope.launch {
                    delay(2500)
                    stopTestSound()
                    onComplete()
                }
            } else {
                onComplete()
            }
        } catch (e: Exception) {
            Log.w("AudioProfileManager", "Error playing test sound: ${e.message}")
            onComplete()
        }
    }

    fun stopTestSound() {
        testStopJob?.cancel()
        testStopJob = null
        try {
            activeTestRingtone?.stop()
        } catch (_: Exception) {}
        activeTestRingtone = null
    }
}
