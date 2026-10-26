package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProfileSettings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SystemModesCard(
    settings: ProfileSettings,
    isDarkTheme: Boolean,
    onSettingsChanged: (ProfileSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("system_modes_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1D162E) else Color(0xFFFFFFFF)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isDarkTheme) Color(0xFF3E315C) else Color(0xFFDDD2ED)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Системні перемикачі режиму",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Активація функцій разом із цим звуковим профілем",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of 8 System Modes
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Mute
                SystemToggleChip(
                    icon = Icons.Default.VolumeMute,
                    title = "Mute (Тиша)",
                    isActive = settings.isMute,
                    isDarkTheme = isDarkTheme,
                    onToggle = { onSettingsChanged(settings.copy(isMute = !settings.isMute)) }
                )

                // 2. DND
                SystemToggleChip(
                    icon = Icons.Default.DoNotDisturbOn,
                    title = "DND (Не турбувати)",
                    isActive = settings.isDnd,
                    isDarkTheme = isDarkTheme,
                    onToggle = { onSettingsChanged(settings.copy(isDnd = !settings.isDnd)) }
                )

                // 3. Vibrate
                SystemToggleChip(
                    icon = Icons.Default.Vibration,
                    title = "Vibrate (Вібро)",
                    isActive = settings.isVibrate,
                    isDarkTheme = isDarkTheme,
                    onToggle = { onSettingsChanged(settings.copy(isVibrate = !settings.isVibrate)) }
                )

                // 4. Dark Mode
                SystemToggleChip(
                    icon = Icons.Default.DarkMode,
                    title = "Dark mode (Темна тема)",
                    isActive = settings.isDarkMode,
                    isDarkTheme = isDarkTheme,
                    onToggle = { onSettingsChanged(settings.copy(isDarkMode = !settings.isDarkMode)) }
                )

                // 5. Auto Brightness
                SystemToggleChip(
                    icon = Icons.Default.BrightnessAuto,
                    title = "Auto brightness",
                    isActive = settings.isAutoBrightness,
                    isDarkTheme = isDarkTheme,
                    onToggle = { onSettingsChanged(settings.copy(isAutoBrightness = !settings.isAutoBrightness)) },
                    systemIntentAction = Settings.ACTION_DISPLAY_SETTINGS
                )

                // 6. Battery Saver
                SystemToggleChip(
                    icon = Icons.Default.BatterySaver,
                    title = "Battery saver",
                    isActive = settings.isBatterySaver,
                    isDarkTheme = isDarkTheme,
                    onToggle = { onSettingsChanged(settings.copy(isBatterySaver = !settings.isBatterySaver)) },
                    systemIntentAction = Settings.ACTION_BATTERY_SAVER_SETTINGS
                )

                // 7. Reading Mode
                SystemToggleChip(
                    icon = Icons.Default.MenuBook,
                    title = "Reading mode",
                    isActive = settings.isReadingMode,
                    isDarkTheme = isDarkTheme,
                    onToggle = { onSettingsChanged(settings.copy(isReadingMode = !settings.isReadingMode)) },
                    systemIntentAction = Settings.ACTION_NIGHT_DISPLAY_SETTINGS
                )

                // 8. Airplane Mode
                SystemToggleChip(
                    icon = Icons.Default.AirplanemodeActive,
                    title = "Airplane mode",
                    isActive = settings.isAirplaneMode,
                    isDarkTheme = isDarkTheme,
                    onToggle = { onSettingsChanged(settings.copy(isAirplaneMode = !settings.isAirplaneMode)) },
                    systemIntentAction = Settings.ACTION_AIRPLANE_MODE_SETTINGS
                )
            }
        }
    }
}

@Composable
private fun SystemToggleChip(
    icon: ImageVector,
    title: String,
    isActive: Boolean,
    isDarkTheme: Boolean,
    onToggle: () -> Unit,
    systemIntentAction: String? = null
) {
    val context = LocalContext.current
    val activeColor = MaterialTheme.colorScheme.primary

    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(14.dp),
        color = if (isActive) activeColor.copy(alpha = if (isDarkTheme) 0.2f else 0.15f) else (if (isDarkTheme) Color(0xFF251C3A) else Color(0xFFF3EDFA)),
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, activeColor) else null,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (systemIntentAction != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Відкрити налаштування системи",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(12.dp)
                        .clickable {
                            try {
                                context.startActivity(Intent(systemIntentAction))
                            } catch (_: Exception) {}
                        }
                )
            }
        }
    }
}
