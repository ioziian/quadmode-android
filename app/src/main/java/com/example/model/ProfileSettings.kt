package com.example.model

data class ProfileSettings(
    val ringPercent: Int = 50,
    val notificationPercent: Int = 50,
    val mediaPercent: Int = 50,
    val boosterPercent: Int = 100, // 100% is normal max, 101-200% is boosted
    val isMute: Boolean = false,
    val isDnd: Boolean = false,
    val isVibrate: Boolean = false,
    val isDarkMode: Boolean = true,
    val isAutoBrightness: Boolean = false,
    val isBatterySaver: Boolean = false,
    val isReadingMode: Boolean = false,
    val isAirplaneMode: Boolean = false,
    val isScheduleEnabled: Boolean = false,
    val scheduleStartTime: String = "22:00",
    val scheduleEndTime: String = "07:00",
    val scheduleDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7) // 1=Mon, 7=Sun
) {
    companion object {
        fun defaultForMode(mode: VolumeMode): ProfileSettings {
            return when (mode) {
                VolumeMode.OUTDOOR -> ProfileSettings(
                    ringPercent = 100,
                    notificationPercent = 100,
                    mediaPercent = 100,
                    boosterPercent = 150,
                    isVibrate = true
                )
                VolumeMode.DAY -> ProfileSettings(
                    ringPercent = 70,
                    notificationPercent = 60,
                    mediaPercent = 75,
                    boosterPercent = 100,
                    isDarkMode = false
                )
                VolumeMode.SILENT -> ProfileSettings(
                    ringPercent = 0,
                    notificationPercent = 0,
                    mediaPercent = 0,
                    boosterPercent = 100,
                    isMute = true,
                    isVibrate = true
                )
                VolumeMode.NIGHT -> ProfileSettings(
                    ringPercent = 0,
                    notificationPercent = 0,
                    mediaPercent = 0,
                    boosterPercent = 100,
                    isDnd = true,
                    isDarkMode = true,
                    isReadingMode = true,
                    isScheduleEnabled = true,
                    scheduleStartTime = "23:00",
                    scheduleEndTime = "07:00"
                )
            }
        }
    }
}
